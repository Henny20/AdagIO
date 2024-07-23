package com.mycompany.mydevice;

class Device {

    private int vendorid;
    private int productid;
    //private String manufacturer;
   // private String productname;
    private int port;
    private String driver;
     
    Device (int vendorid, int productid, int port, String productname) {
       this.vendorid = vendorid;
       this.productid = productid;
       this.port = port;
       this.driver = driver;
      // this.manufacturer = manufacturer;
       //this.productname = productname;
    }
    
    public int getVendorId() { return vendorid;  }
    
    public int getProductId() { return productid;  }
    
    public int getPort() { return port;  }
    
    public String getDriver() { return driver;  }

    //public String getManufacturerName() { return manufacturer; }

   // public String getProductName() { return productname; }
  
}
