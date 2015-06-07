using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

public class ServiceException : ApplicationException
{
    public ServiceException(string information)
        : base(information)
    {

    }

    public ServiceException(string information, string exceptionMessage)
        : base(information + " (" + exceptionMessage + ")")
    {

    }
}