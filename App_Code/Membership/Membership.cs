using System;
using System.Collections.Generic;
using System.Linq;
using System.Security.Cryptography;
using System.Web;
using TheBiletApp.Classes;

namespace TheBilet.Membership
{
    public class Membership
    {
        public static String RegisterAccount(String username, String password, String email)
        {
            using (var context = new use_thebiletEntities())
            {
                var emailTestQuery = context.Drivers.Where(s => s.Email == email);
                var usernameTestQuery = context.Drivers.Where(s => s.Username == username);

                //Test email 
                if (emailTestQuery.Count<Driver>() > 0)
                    throw new ServiceException("Sorry, this e-mail is taken");

                //Test username
                if (usernameTestQuery.Count<Driver>() > 0)
                    throw new ServiceException("Sorry, thisusername is taken");

                //All OK, now register
                Driver NewDriver = new Driver();
                NewDriver.Username = username;
                NewDriver.PasswordHash = ComputeHash(password);
                NewDriver.Email = email;

                context.Drivers.Add(NewDriver);
                context.SaveChanges();

                return "OK";
            }
        }

        public static String Authenticate(String username, String password, String deviceId)
        {
            using (var context = new use_thebiletEntities())
            {
                String PasswordHash = ComputeHash(password);

                var memberAuthQuery = context.Drivers.Where(s => s.Username == username).Where(
                    s => s.PasswordHash == PasswordHash);
          
                //Test credentails 
                if (memberAuthQuery.Count<Driver>() != 1)
                    throw new ServiceException("Invalid credentials");
      
                //Our user
                Driver User = memberAuthQuery.First<Driver>();

                //Remove old tokens, device recognition might be added here
                context.AuthTokens.RemoveRange(context.AuthTokens.Where(s => s.UserId == User.Id).Where(s => s.IsGuard == false));

                //All OK, now create token and return it
                AuthToken NewToken = new AuthToken();
                NewToken.UserId = User.Id;
                NewToken.IsGuard = false;
                NewToken.Token = GenerateAuthToken(username, password, deviceId);
                NewToken.ExpiresOn = DateTime.Now.AddYears(1);

                context.AuthTokens.Add(NewToken);
                context.SaveChanges();

                return NewToken.Token;
            }
        }

        public static String AuthenticateController(String username, String password, String deviceId)
        {
            using (var context = new use_thebiletEntities())
            {
                String PasswordHash = ComputeHash(password);

                var memberAuthQuery = context.Controllers.Where(s => s.Username == username).Where(
                    s => s.PasswordHash == PasswordHash);

                //Test credentails 
                if (memberAuthQuery.Count<Controller>() != 1)
                    throw new ServiceException("Invalid credentials");

                //Our user
                Controller User = memberAuthQuery.First<Controller>();

                //Remove old tokens, device recognition might be added here
                context.AuthTokens.RemoveRange(context.AuthTokens.Where(s => s.UserId == User.Id).Where(s => s.IsGuard == true));

                //All OK, now create token and return it
                AuthToken NewToken = new AuthToken();
                NewToken.UserId = User.Id;
                NewToken.IsGuard = true;
                NewToken.Token = GenerateAuthToken(username, password, deviceId);
                NewToken.ExpiresOn = DateTime.Now.AddMonths(1);

                context.AuthTokens.Add(NewToken);
                context.SaveChanges();

                return NewToken.Token;
            }
        }

        public static String ComputeHash(string text)
        {
            var sha1Provider = HashAlgorithm.Create("SHA512");
            var binHash = sha1Provider.ComputeHash(System.Text.Encoding.Unicode.GetBytes(text));
            var base64HashOutput = Convert.ToBase64String(binHash);
            return base64HashOutput;
        }

        public static String GenerateAuthToken(string username, string password, string deviceId)
        {
            var sha1Provider = HashAlgorithm.Create("SHA512");
            var binHash = sha1Provider.ComputeHash(System.Text.Encoding.Unicode.GetBytes(username + DateTime.Now + password + deviceId));
            var base64HashOutput = Convert.ToBase64String(binHash);

            return base64HashOutput.Replace("=","").Replace("+","").Replace("-","").Replace("/","");
        }
    }
}