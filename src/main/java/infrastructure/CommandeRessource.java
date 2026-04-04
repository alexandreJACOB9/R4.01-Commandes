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

    // Injection (ou instanciation simple pour le TP) du service
    private CommandeService service = new CommandeService();

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
}