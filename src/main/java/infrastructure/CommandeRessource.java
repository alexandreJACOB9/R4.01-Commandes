package infrastructure;

import application.CommandeService;
import domain.Commande;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/commandes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CommandeRessource {

    private final CommandeService service = new CommandeService(new InMemoryCommandeRepository());

    @GET
    public List<Commande> getAll(@QueryParam("abonneId") Long abonneId) {
        return service.listerToutesLesCommandes(abonneId);
    }

    @POST
    public Response create(CommandeInput input) {
        try {
            Commande nouvelle = service.creerNouvelleCommande(input);
            return Response.status(Response.Status.CREATED).entity(nouvelle).build();
        } catch (Exception e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Erreur métier : " + e.getMessage()).build();
        }
    }
    @GET @Path("/{id}")
    public Response getById(@PathParam("id") Long id) {
        return service.recupererParId(id)
                .map(c -> Response.ok(c).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @PUT @Path("/{id}")
    public Response update(@PathParam("id") Long id, CommandeUpdateInput input) {
        return service.modifierCommande(id, input)
                .map(c -> Response.ok(c).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @DELETE @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        if (service.supprimerCommande(id)) {
            return Response.noContent().build(); // 204 No Content
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }
}