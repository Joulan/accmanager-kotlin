package rest
import service.AccountServiceInt
import javax.inject.Inject
import javax.ws.rs.Consumes
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response


@Path("accounts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class AccountResource
@Inject constructor(private val accountService: AccountServiceInt) {

    @POST
    @Path("send")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    fun sendMoney(moneyTransferDto: MoneyTransferDto): Response {
        try {
            accountService.runTransfer(moneyTransferDto)
        } catch (e: Throwable) {
            println(e)
            return Response.status(Response.Status.NOT_ACCEPTABLE).entity(ResponseMessage(e.message)).build()
        }
        return Response.ok(ResponseMessage("ok")).build()
    }

}

data class ResponseMessage(val message: String?)
