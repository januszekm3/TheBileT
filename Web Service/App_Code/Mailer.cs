using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Net.Mail;
using System.Net;

namespace TheBiletApp.App_Code
{
    public class Mailer
    {
        private const string NoreplyEmail = "noreply@thebilet.usetitan.com";
        private const string EmailLogin = "noreply@thebilet.usetitan.com";
        private const string EmailPassword = "supersilnehaslo123";
        private const int EmailPort = 21;
        private const string EmailHost = "mail.usetitan.com";

        public static void SendPasswordEmail(string email, string newPassword)
        {
            MailMessage message = new MailMessage();
            message.From = new MailAddress(NoreplyEmail);
            message.To.Add(email);

            message.Subject = "Password reset";
            message.Body = "Your new password is: " + newPassword;
            message.IsBodyHtml = true;

            SmtpClient client = new SmtpClient();

            client.Port = EmailPort;
            client.Host = EmailHost;
            client.Credentials = new NetworkCredential(EmailLogin, EmailPassword);

            client.Send(message);
        }
    }
}