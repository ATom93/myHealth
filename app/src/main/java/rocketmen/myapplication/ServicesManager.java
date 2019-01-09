package rocketmen.myapplication;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by luigi on 06/12/2018.
 */

public class ServicesManager implements Serializable{


    ArrayList<Service> services;

    public ServicesManager(){
        services = new ArrayList<>();
    }

    public ServicesManager(Service[] services){
        this.services = new ArrayList<>();

        for(int i=0;i<services.length;i++){
            this.services.add(services[i]);
        }
    }

    public void addService(Service service){
        services.add(service);
    }

    public Service getServiceByName(String name){
        for(int i=0;i<services.size();i++){
            if(services.get(i).name.equals(name)){
                return services.get(i);
            }
        }

        return null;
    }

    public Service[] getServicesAsArray(){
        Service[] services = new Service[this.services.size()];
        services = this.services.toArray(services);

        return services;
    }

    public void removeServiceByName(String name){
        for(int i=0;i<services.size();i++){
            if(services.get(i).name.equals(name)){
                services.remove(i);
                break;
            }
        }
    }


}
