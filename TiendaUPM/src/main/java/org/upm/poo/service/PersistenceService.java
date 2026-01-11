package org.upm.poo.service;

import com.google.gson.*;
import org.upm.poo.domain.*;
import org.upm.poo.domain.user.*;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Collection;

public class PersistenceService {
    private static final String DATA_FILE = "tienda_upm_data.json";
    private final Gson gson;

    public PersistenceService() {
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Product.class, new ProductDeserializer())
                .registerTypeAdapter(LocalDate.class, (JsonDeserializer<LocalDate>) (json, type, context) -> LocalDate.parse(json.getAsString()))
                .registerTypeAdapter(LocalDate.class, (JsonSerializer<LocalDate>) (src, type, context) -> new JsonPrimitive(src.toString()))
                .create();
    }

    private static class AppState {
        Collection<Product> products;
        Collection<Client> clients;
        Collection<Cashier> cashiers;
        Collection<Ticket> tickets;
    }

    public void save(Catalog catalog, UserRegistry userRegistry, TicketService ticketService) {
        try (Writer writer = new FileWriter(DATA_FILE)) {
            AppState state = new AppState();
            state.products = catalog.list();
            state.clients = userRegistry.listClients();
            state.cashiers = userRegistry.listCashiers();
            state.tickets = ticketService.findAll();

            gson.toJson(state, writer);
            System.out.println("Data saved successfully to " + DATA_FILE);
        } catch (IOException e) {
            System.err.println("Error saving data: " + e.getMessage());
        }
    }

    public void load(Catalog catalog, UserRegistry userRegistry, TicketService ticketService) {
        if (!Files.exists(Path.of(DATA_FILE))) return;

        try (Reader reader = new FileReader(DATA_FILE)) {
            AppState state = gson.fromJson(reader, AppState.class);
            if (state == null) return;

            if (state.products != null) state.products.forEach(catalog::add);

            if (state.clients != null) state.clients.forEach(userRegistry::addClient);
            if (state.cashiers != null) state.cashiers.forEach(userRegistry::addCashier);

            if (state.tickets != null) {
                for (Ticket t : state.tickets) {
                    Client c = userRegistry.getClient(t.getClientId());
                    if (c.isCompany()) t.setPolicy(new EnterpriseTicketPolicy());
                    else t.setPolicy(new StandardTicketPolicy());

                    ticketService.loadTicketRaw(t);
                }
            }
            System.out.println("Data loaded successfully.");
        } catch (Exception e) {
            System.err.println("Error loading data: " + e.getMessage());
        }
    }

    private static class ProductDeserializer implements JsonDeserializer<Product> {
        @Override
        public Product deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            JsonElement typeElem = jsonObject.get("type");

            if (typeElem == null) return context.deserialize(jsonObject, StandardProduct.class);

            String type = typeElem.getAsString();
            try {
                return context.deserialize(json, Class.forName("org.upm.poo.domain." + type));
            } catch (ClassNotFoundException e) {
                throw new JsonParseException("Unknown product type: " + type, e);
            }
        }
    }
}