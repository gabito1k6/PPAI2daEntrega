package com.utn.bolsines.servicios;

public class GPSTrackerService {

    private static GPSTrackerService instancia;

    private GPSTrackerService() {}

    public static GPSTrackerService getInstancia() {
        if (instancia == null) {
            instancia = new GPSTrackerService();
        }
        return instancia;
    }

    /**
     * Obtiene la ubicación actual del bolsin/vehículo rastreado.
     * @param idDispositivo identificador del dispositivo GPS
     * @return coordenadas como String (ej: "lat,lng")
     */
    public String obtenerUbicacion(String idDispositivo) {
        // TODO: integrar con API de GPS Tracker real
        System.out.println("Consultando GPS para dispositivo: " + idDispositivo);
        return "-34.6037,-58.3816"; // placeholder Buenos Aires
    }
}
