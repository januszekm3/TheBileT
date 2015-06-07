<%@ WebHandler Language="C#" Class="web" %>

using System;
using System.Web;
using TheBilet.Membership;
using TheBilet.Ticket;

public class web : IHttpHandler {
    
    public void ProcessRequest (HttpContext context) {
        
        context.Response.ContentType = "text/plain";

        try
        {
            if (context.Request.PathInfo.Length == 0)
                throw new ServiceException("No command set");

            ServiceCommand Command = (ServiceCommand)Enum.Parse(typeof(ServiceCommand), context.Request.PathInfo.Substring(1), true);

            switch (Command)
            {
                case ServiceCommand.register:
                    context.Response.Write(MembershipUtils.RegisterAccount(context));
                    break;
                case ServiceCommand.login:
                    context.Response.Write(MembershipUtils.Authenticate(context));
                    break;
                case ServiceCommand.loginController:
                    context.Response.Write(MembershipUtils.AuthenticateController(context));
                    break;
                case ServiceCommand.buyTicket:
                    context.Response.Write(TicketUtils.buyTicket(context));
                    break;
                case ServiceCommand.checkTicket:
                    context.Response.Write(TicketUtils.checkTicket(context));
                    break;
                case ServiceCommand.listTickets:
                    context.Response.Write(TicketUtils.listTickets(context));
                    break;
                case ServiceCommand.getBalance:
                    context.Response.Write(TicketUtils.getBalance(context));
                    break;
            }

        }
        catch (ServiceException ex)
        {
            context.Response.Write("ERROR: " + ex.Message);
        }
        catch (Exception ex)
        {
            context.Response.Write("APP ERROR: " + ex.Message);
        }
               
    }
 
    public bool IsReusable {
        get {
            return false;
        }
    }

}