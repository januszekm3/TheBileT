using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace TheBilet.Ticket
{
    public class TicketUtils
    {
        public static String VAR_TICKET_ID = "ticketId";
        public static String VAR_REGISTRATION_NUMBER = "registrationNumber";
        public static String VAR_START_DATE = "startDate";
        public static String VAR_END_DATE = "endDate";
        public static String VAR_AREA_ID = "areaId";
        public static String VAR_DRIVER_ID = "driverId";
        public static String VAR_TOKEN = "authToken";

        public static String buyTicket(HttpContext context)
        {
            return parseArguments(context, ServiceCommand.buyTicket);
        }

        public static String checkTicket(HttpContext context)
        {
            return parseArguments(context, ServiceCommand.checkTicket);
        }

        public static String getBalance(HttpContext context)
        {
            return parseArguments(context, ServiceCommand.getBalance);
        }

        public static String listTickets(HttpContext context)
        {
            return parseArguments(context, ServiceCommand.listTickets);
        }

        public static String parseArguments(HttpContext context, ServiceCommand command)
        {
            try
            {
                switch (command)
                {
                    case ServiceCommand.buyTicket:
                        return Tickets.BuyTicket(context.Request.Params[VAR_REGISTRATION_NUMBER], Convert.ToDateTime(context.Request.Params[VAR_START_DATE]),
                        Convert.ToDateTime(context.Request.Params[VAR_END_DATE]), Convert.ToInt32(context.Request.Params[VAR_AREA_ID]), context.Request.Params[VAR_TOKEN].ToString());

                    case ServiceCommand.checkTicket:
                        return Tickets.CheckTicket(context.Request.Params[VAR_REGISTRATION_NUMBER].ToString(), Convert.ToInt32(context.Request.Params[VAR_AREA_ID]), 
                            context.Request.Params[VAR_TOKEN].ToString());

                    case ServiceCommand.listTickets:
                        return Tickets.ListTickets(context.Request.Params[VAR_TOKEN].ToString());

                    case ServiceCommand.getBalance:
                        return Tickets.GetBalance(context.Request.Params[VAR_TOKEN].ToString());

                    default:
                        return "Unknown command";
                }
            }
            catch (NullReferenceException ex)
            {
                throw new ServiceException("Some arguments are missing", ex.Message);
            }
            catch (FormatException ex)
            {
                throw new ServiceException("Invalid argument format", ex.Message);
            }
        }
    }
}