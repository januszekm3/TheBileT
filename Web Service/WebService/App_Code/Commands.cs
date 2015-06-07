﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;


public enum ServiceCommand
{
    register = 0,
    login = 1,
    loginController = 6,

    checkTicket = 3,
    buyTicket = 4,

    listTickets = 5,
    getBalance = 7
}