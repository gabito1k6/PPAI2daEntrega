package com.utn.bolsines.servicios;

public class GoogleMapsService {

    private static GoogleMapsService instancia;

    private GoogleMapsService() {}

    public static GoogleMapsService getInstancia() {
        if (instancia == null) {
            instancia = new GoogleMapsService();
        }
        return instancia;
    }

    /**
     * Calcula la ruta entre dos puntos.
     * @param origen coordenadas de origen
     * @param destino coordenadas de destino
     * @return descripción de la ruta
     */
    public String calcularRuta(String origen, String destino) {
        // TODO: integrar con Google Maps API real
        System.out.println("Calculando ruta de " + origen + " a " + destino);
        return "Ruta calculada: " + origen + " -> " + destino;
    }

    /**
     * Estima el tiempo de llegada dado un origen y destino.
     * @param origen coordenadas de origen
     * @param destino coordenadas de destino
     * @return tiempo estimado en minutos
     */
    public int estimarTiempoLlegada(String origen, String destino) {
        // TODO: integrar con Google Maps API real
        return 30; // placeholder: 30 minutos
    }
}
