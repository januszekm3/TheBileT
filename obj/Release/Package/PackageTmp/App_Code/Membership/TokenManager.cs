using System;
using System.Collections.Generic;
using System.Linq;
using System.Security.Cryptography;
using System.Text;
using System.Web;
using TheBiletApp.Classes;


namespace TheBiletApp.App_Code.Membership
{
    public class TokenManager
    {
        /// <summary>
        /// Returns UserId of the corresponding token (if valid & active)
        /// </summary>
        /// <param name="token"></param>
        /// <returns></returns>
        public static int Parse(String token)
        {
            using (var context = new use_thebiletEntities())
            {
                var tokenQuery = context.AuthTokens.Where(s => s.Token == token).Where(s => s.IsGuard == false);
   
                //No token
                if (tokenQuery.Count<AuthToken>() <= 0)
                    throw new ServiceException("Invalid authentication token");

                AuthToken Token = tokenQuery.First<AuthToken>();

                //Test valid tame
                if (Token.ExpiresOn < DateTime.Now)
                    throw new ServiceException("This token has expired");

                //All OK, return userId
                return Token.UserId;
            }
        }

        /// <summary>
        /// Returns TRUE if the corresponding token is valid & active
        /// </summary>
        /// <param name="token"></param>
        /// <returns></returns>
        public static bool ParseController(String token)
        {
            using (var context = new use_thebiletEntities())
            {
                var tokenQuery = context.AuthTokens.Where(s => s.Token == token).Where(s => s.IsGuard == true);

                //No token
                if (tokenQuery.Count<AuthToken>() <= 0)
                    throw new ServiceException("Invalid authentication token");

                AuthToken Token = tokenQuery.First<AuthToken>();

                //Test valid tame
                if (Token.ExpiresOn < DateTime.Now)
                    throw new ServiceException("This token has expired");

                //All OK, return true
                return true;
            }
        }
    }
}