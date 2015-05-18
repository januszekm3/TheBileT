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

        public static String AuthenticateController(HttpContext context)
        {
            return parseArguments(context, ServiceCommand.loginController);
        }

        public static String parseArguments(HttpContext context, ServiceCommand command)
        {
            try
            {
                switch (command)
                {
                    case ServiceCommand.register:
                        return Membership.RegisterAccount(context.Request.Params[VAR_USERNAME].ToString(), context.Request.Params[VAR_PASSWORD].ToString(), context.Request.Params[VAR_EMAIL].ToString());

                    case ServiceCommand.login:
                        return Membership.Authenticate(context.Request.Params[VAR_USERNAME].ToString(), context.Request.Params[VAR_PASSWORD].ToString());

                    case ServiceCommand.loginController:
                        return Membership.AuthenticateController(context.Request.Params[VAR_USERNAME].ToString(), context.Request.Params[VAR_PASSWORD].ToString());

                    default:
                        return "Unknown command";
                }
            }
            catch (NullReferenceException ex)
            {
                throw new ServiceException("Some arguments are missing");
            }
            catch (FormatException ex)
            {
                throw new ServiceException("Invalid argument format");
            }
        }
    }
}