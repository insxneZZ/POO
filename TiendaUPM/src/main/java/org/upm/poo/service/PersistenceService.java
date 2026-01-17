package org.upm.poo.service;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.upm.poo.domain.*;
import org.upm.poo.domain.user.*;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

public class PersistenceService {
    private static final String DATA_FILE = "tienda_upm_data.json";
    private final Gson gson;

    public PersistenceService() {
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Product.class, new ProductSerializer())
                .registerTypeAdapter(Product.class, new ProductDeserializer())
                .registerTypeAdapter(Ticket.class, new TicketDeserializer())
                .registerTypeAdapter(LocalDate.class, (JsonDeserializer<LocalDate>) (json, type, context) -> LocalDate.parse(json.getAsString()))
                .registerTypeAdapter(LocalDate.class, (JsonSerializer<LocalDate>) (src, type, context) -> new JsonPrimitive(src.toString()))
                .create();
    }

    private static class AppState {
        Collection<Product> products;
        Collection<Client> clients;
        Collection<Cashier> cashiers;
        JsonElement tickets;
    }

    public void save(Catalog catalog, UserRegistry userRegistry, TicketService ticketService) {
        try (Writer writer = new FileWriter(DATA_FILE)) {
            AppState state = new AppState();
            state.products = catalog.list();
            state.clients = userRegistry.listClients();
            state.cashiers = userRegistry.listCashiers();
            state.tickets = gson.toJsonTree(ticketService.findAll());

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


            if (state.tickets != null && !state.tickets.isJsonNull()) {
                Type ticketListType = new TypeToken<List<Ticket>>(){}.getType();
                List<Ticket> loadedTickets = gson.fromJson(state.tickets, ticketListType);

                for (Ticket t : loadedTickets) {
                    ticketService.loadTicketRaw(t);
                }
            }
            System.out.println("Data loaded successfully.");
        } catch (Exception e) {
            System.err.println("Error loading data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // --- ADAPTADORES ---

    private static class ProductSerializer implements JsonSerializer<Product> {
        @Override
        public JsonElement serialize(Product src, Type typeOfSrc, JsonSerializationContext context) {

            JsonObject obj = new GsonBuilder()
                    .registerTypeAdapter(LocalDate.class, (JsonSerializer<LocalDate>) (d, t, c) -> new JsonPrimitive(d.toString()))
                    .create().toJsonTree(src).getAsJsonObject();
            obj.addProperty("type", src.getClass().getSimpleName());
            return obj;
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
                // Instancia la clase correcta (ServiceProduct, Food, etc.)
                return context.deserialize(json, Class.forName("org.upm.poo.domain." + type));
            } catch (ClassNotFoundException e) {
                throw new JsonParseException("Unknown product type: " + type, e);
            }
        }
    }

    private static class TicketDeserializer implements JsonDeserializer<Ticket> {
        @Override
        public Ticket deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject obj = json.getAsJsonObject();
            String id = obj.get("id").getAsString();
            String cashierId = obj.get("cashierId").getAsString();
            String clientId = obj.get("clientId").getAsString();

            Client client = UserRegistry.getInstance().getClient(clientId);

            Ticket ticket;
            if (client.isCompany()) {
                ticket = new EnterpriseTicket(id, cashierId, clientId);
            } else {
                ticket = new StandardTicket(id, cashierId, clientId);
            }

            TicketState state = TicketState.EMPTY;
            if (obj.has("state")) state = context.deserialize(obj.get("state"), TicketState.class);

            List<LineItem> items = null;
            if (obj.has("items")) items = context.deserialize(obj.get("items"), new TypeToken<List<LineItem>>(){}.getType());

            ticket.loadFromPersistence(state, items);
            return ticket;
        }
    }
}