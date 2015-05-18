using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace TheBiletApp.Classes
{
    public partial class Ticket
    {
        public bool IsActive
        {
            get
            {
                return StartDate < DateTime.Now && EndDate > DateTime.Now;
            }
        }

        public Decimal CalculatePrice(Area TargetArea)
        {
            Decimal Price = TargetArea.StartPrice;
            Decimal HoursInTicket = Convert.ToDecimal((EndDate - StartDate).TotalHours);

            Price += HoursInTicket * TargetArea.PricePerHour;
            Price = Decimal.Round(Price, 2);

            return Price;
        }
    }
}