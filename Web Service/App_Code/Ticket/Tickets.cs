using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using TheBiletApp.Classes;
using TheBiletApp.App_Code.Membership;
using System.Data.Entity.Validation;
using System.Diagnostics;

namespace TheBilet.Ticket
{
    public class Tickets
    {
        public static String RegistrationNumberJSON = "registrationNumber";
        public static String StartDateJSON = "startDate";
        public static String EndDateJSON = "endDate";
        public static String AreaIdJSON = "areaId";


        public static String BuyTicket(String registrationNumber, DateTime startDate, DateTime endDate, int areaID, String authToken)
        {
            int UserId = TokenManager.Parse(authToken);

            using (var context = new use_thebiletEntities())
            {
                //Our user
                Driver User = context.Drivers.Where(s => s.Id == UserId).First<Driver>();

                //Create ticket
                var NewTicket = new TheBiletApp.Classes.Ticket();
                NewTicket.StartDate = startDate;
                NewTicket.EndDate = endDate;
                NewTicket.AreaId = areaID;
                NewTicket.DriverId = User.Id;
                NewTicket.RegistrationNumber = registrationNumber;

                //Get target area
                var areaQuery = context.Areas.Where(s => s.Id == NewTicket.AreaId);

                if (areaQuery.Count<Area>() != 1)
                    throw new ServiceException("There is no area with that id");

                Decimal TicketPrice = NewTicket.CalculatePrice(areaQuery.First<Area>());

                //Get ticket price
                if (User.Balance < TicketPrice)
                    throw new ServiceException("You don't have enough funds on your balance. Required amount: " + TicketPrice);

                //Deduce amount and add ticket
                User.Balance -= TicketPrice;

                context.Tickets.Add(NewTicket);

                context.SaveChanges();

                return "OK";
            }

        }

        public static String CheckTicket(String registrationNumber, int areaID, String authToken)
        {
            TokenManager.ParseController(authToken);

            using (var context = new use_thebiletEntities())
            {
                var Query = context.Tickets.Where(s => s.RegistrationNumber == registrationNumber);

                if (Query.Count<TheBiletApp.Classes.Ticket>() <= 0)
                    throw new ServiceException("No parking ticket(s) for the specified registration number: " + registrationNumber);

                Query = Query.Where(s => s.StartDate < DateTime.Now).Where(s => s.EndDate > DateTime.Now);

                if (Query.Count<TheBiletApp.Classes.Ticket>() <= 0)
                    throw new ServiceException("Parking ticket(s) expired");

                return "OK";
            }
        }

        public static String ListTickets(String authToken)
        {
            int UserId = TokenManager.Parse(authToken);

            using (var context = new use_thebiletEntities())
            {
                var Query = context.Tickets.Where(s => s.DriverId == UserId).Where(s => s.EndDate > DateTime.Now).OrderBy(s => s.StartDate);

                
                JArray json = new JArray(from p in Query.ToList<TheBiletApp.Classes.
                                             Ticket>()
                                  select new JObject(
                                      new JProperty(RegistrationNumberJSON, p.RegistrationNumber),
                                      new JProperty(StartDateJSON, p.StartDate),
                                      new JProperty(EndDateJSON, p.EndDate),
                                      new JProperty(AreaIdJSON, p.AreaId)
                                      )
                    );

                return json.ToString();
            }
        }

        public static String GetBalance(String authToken)
        {
            int UserId = TokenManager.Parse(authToken);

            using (var context = new use_thebiletEntities())
            {
                Driver User = context.Drivers.Where(s => s.Id == UserId).First<Driver>();
                return User.Balance.ToString("0.##");
            }
        }
    }
}