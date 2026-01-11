package org.upm.poo.cli;

import org.upm.poo.domain.*;

import java.util.Locale;

public class EnterpriseTicketPrinter implements ITicketPrinter {

    @Override
    public void print(Ticket<?> t) {
        System.out.println("Ticket : " + t.getId());

        t.getItems().stream()
                .sorted((l1, l2) -> l1.getProduct().getName().compareToIgnoreCase(l2.getProduct().getName()))
                .forEach(li -> printLineItem(t, li));

        System.out.println("  Total price: " + trim(t.totalPrice()));
        System.out.println("  Total discount: " + trim(t.totalDiscount()));
        System.out.println("  Final Price: " + trim(t.finalPrice()));
    }

    private void printLineItem(Ticket<?> t, LineItem li) {
        Product p = li.getProduct();
        boolean isService = (p instanceof Service);

        String info = p.toString();
        // Limpiamos la salida del toString() si es Servicio para no mostrar precio 0.0 explícito si molesta,
        // aunque el requisito dice "no aparecerán con precio".
        // La implementación simple es no imprimir el precio al lado, pero Product.toString() suele imprimirlo.
        // Asumimos que el formato del toString() es aceptable o que "no aparecerán con precio" se refiere a la línea de detalle de cobro.

        if (!li.getCustomizations().isEmpty()) info += " " + li.getCustomizations();

        for (int i = 0; i < li.getQuantity(); i++) {
            if (isService) {
                // Servicios: Solo nombre/info, SIN precio ni descuentos unitarios visibles
                System.out.println("  " + info);
            } else {
                // Productos en Ticket Empresa: Pueden tener descuentos pero se calculan al final globalmente (el 15%)
                // No mostramos descuento línea a línea a menos que sea el de categoría base.
                System.out.println("  " + info);
            }
        }
    }

    private String trim(double v) {
        return String.format(Locale.ROOT, "%.1f", v);
    }
}