package org.upm.poo;

import org.upm.poo.cli.CommandLoop;
import org.upm.poo.io.AppPersister;
import org.upm.poo.service.Catalog;
import org.upm.poo.service.TicketService;
import org.upm.poo.service.UserRegistry;

public class Main {
    public static void main(String[] args) {
        Catalog catalog = new Catalog();
        UserRegistry userRegistry = new UserRegistry();
        TicketService ticketService = new TicketService(userRegistry);

        Object state = AppPersister.load();
        if (state != null) {
            AppPersister.getProducts(state).forEach(catalog::add);

            AppPersister.getCustomers(state).forEach(c -> {

                if (c instanceof org.upm.poo.domain.user.Client cl) userRegistry.addClient(cl);
                else if (c instanceof org.upm.poo.domain.user.Company co) userRegistry.addCompany(co);
            });
            AppPersister.getCashiers(state).forEach(userRegistry::addCashier);

            AppPersister.getTickets(state).forEach(ticketService::registerTicket);
        }


        String inputFile = (args.length > 0) ? args[0] : null;
        new CommandLoop(catalog, ticketService, userRegistry).run(inputFile);


        AppPersister.save(
                catalog.list(),
                combine(userRegistry.listClients(), userRegistry.listCompanies()),
                userRegistry.listCashiers(),
                ticketService.findAll()
        );
    }

    private static java.util.Collection<org.upm.poo.domain.user.Customer> combine(
            java.util.Collection<org.upm.poo.domain.user.Client> l1,
            java.util.Collection<org.upm.poo.domain.user.Company> l2) {
        java.util.List<org.upm.poo.domain.user.Customer> all = new java.util.ArrayList<>();
        all.addAll(l1);
        all.addAll(l2);
        return all;
    }
}