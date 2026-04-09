package infrastructure;

import application.OrderInput;
import application.OrderService;
import application.OrderUpdateInput;
import domain.Order;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

/**
 * Expose les endpoints REST de gestion des commandes.
 */
@Path("/orders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrderResource {

    private final OrderService service = new OrderService(new JdbcOrderRepository());
    /**
     * Point d'entrée REST pour lister les commandes.
     * Peut être filtré par abonné via le QueryParam "?subscriberId=".
     *
     * @param subscriberId (Optionnel) L'ID de l'abonné pour filtrer les résultats.
     * @return La liste des commandes au format JSON.
     */
    @GET
    public List<Order> getAll(@QueryParam("subscriberId") Long subscriberId) {
        return service.listOrders(subscriberId);
    }

    /**
     * Point d'entrée REST pour créer une nouvelle commande.
     *
     * @param input Les données de la commande envoyées dans le corps de la requête (Body).
     * @return Une réponse HTTP 201 (Created) contenant la commande créée,
     * ou 400 (Bad Request) si les règles métier ne sont pas respectées.
     */
    @POST
    public Response create(OrderInput input) {
        try {
            Order createdOrder = service.createOrder(input);
            return Response.status(Response.Status.CREATED).entity(createdOrder).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Business error: " + e.getMessage()).build();
        }
    }

    /**
     * Point d'entrée REST pour récupérer les détails d'une commande spécifique.
     *
     * @param id L'identifiant de la commande passé dans l'URL.
     * @return Une réponse HTTP 200 (OK) contenant la commande, ou 404 (Not Found) si elle n'existe pas.
     */
    @GET @Path("/{id}")
    public Response getById(@PathParam("id") Long id) {
        return service.findOrder(id)
                .map(order -> Response.ok(order).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    /**
     * Point d'entrée REST pour modifier l'adresse ou la date de livraison d'une commande.
     *
     * @param id L'identifiant de la commande passé dans l'URL.
     * @param input Les nouvelles informations de livraison (Body).
     * @return Une réponse HTTP 200 (OK) contenant la commande modifiée, ou 404 (Not Found).
     */
    @PUT @Path("/{id}")
    public Response update(@PathParam("id") Long id, OrderUpdateInput input) {
        return service.updateOrder(id, input)
                .map(order -> Response.ok(order).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    /**
     * Point d'entrée REST pour supprimer une commande.
     *
     * @param id L'identifiant de la commande à supprimer passé dans l'URL.
     * @return Une réponse HTTP 204 (No Content) en cas de succès, ou 404 (Not Found).
     */
    @DELETE @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        if (service.deleteOrder(id)) {
            return Response.noContent().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }


}
