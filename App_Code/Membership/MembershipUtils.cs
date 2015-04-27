using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace TheBilet.Membership
{
    public class MembershipUtils
    {
        public static String VAR_USERNAME = "username";
        public static String VAR_PASSWORD = "password";
        public static String VAR_EMAIL = "email";


        public static String RegisterAccount(HttpContext context)
        {
            return parseArguments(context, ServiceCommand.register);
        }

        public static String Authenticate(HttpContext context)
        {
            return parseArguments(context, ServiceCommand.login);
        }

        public static String parseArguments(HttpContext context, ServiceCommand command)
        {
            try
            {
                switch (command)
                {
                    case ServiceCommand.register:
                        return Membership.RegisterAccount(context.Request.Params[VAR_USERNAME], context.Request.Params[VAR_PASSWORD], context.Request.Params[VAR_EMAIL]);

                    case ServiceCommand.login:
                        return Membership.Authenticate(context.Request.Params[VAR_USERNAME], context.Request.Params[VAR_PASSWORD]);

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