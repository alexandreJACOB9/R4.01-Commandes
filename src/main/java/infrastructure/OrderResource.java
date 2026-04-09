package infrastructure;

import application.OrderService;
import domain.Order;
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

    @GET
    public List<Order> getAll(@QueryParam("subscriberId") Long subscriberId) {
        return service.listOrders(subscriberId);
    }

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

    @GET @Path("/{id}")
    public Response getById(@PathParam("id") Long id) {
        return service.findOrder(id)
                .map(order -> Response.ok(order).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @PUT @Path("/{id}")
    public Response update(@PathParam("id") Long id, OrderUpdateInput input) {
        return service.updateOrder(id, input)
                .map(order -> Response.ok(order).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @DELETE @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        if (service.deleteOrder(id)) {
            return Response.noContent().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }


}
