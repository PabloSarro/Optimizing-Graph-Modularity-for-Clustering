package cat.urv.deim;

import java.io.IOException;

// Els imports següents, es fan servir en cas de voler descomentar els dos mètodes auxiliars inicials:

// import java.util.Iterator;
// import cat.urv.deim.exceptions.PosicioForaRang;

// --------------------------------------------------------------------------------------------------------------------------------
// ------------------------------------- L'EXPLICACIÓ DEL PROGRAMA ES TROBA A L'ARXIU README --------------------------------------
// --------------------------------------------------------------------------------------------------------------------------------

public class Main {

    // ---------------- Mètode auxiliar per comprovar una correcta lectura del graf: --------------------------------

    // public static void GrafComprovacions(GrafPajek grafPajek) {
    //     try {
    //         System.out.println("---------------COMPROVACIONS DEL GRAF:---------------");
    //         System.out.println("Vèrtexs del graf: " + grafPajek.numVertexs());
    //         System.out.println("Arestes del graf: " + grafPajek.numArestes());
    //         System.out.print("Llista de vèrtexs del graf: ");
    //         ILlistaGenerica<Integer> personesIDs = grafPajek.obtenirVertexIDs();
    //         for (int i = 0; i < personesIDs.numElements(); i++) {
    //             System.out.print(personesIDs.consultar(i) + " ");
    //         }
    //     } catch (PosicioForaRang e) {
    //         System.out.println("Excepció PosicioForaRang en el mètode GrafComprovacions");
    //     }
    // }

    
    // ---------------- Mètode auxiliar per comprovar la llista de vèrtexs de cada comunitat del graf: ----------------

    // public static void imprimirComs(GrafPajek grafPajek) {
    //     System.out.println();
    //     for (int j = 0; j < grafPajek.llista_coms.size(); j++) {
    //         LlistaNoOrdenada<Integer> llista = grafPajek.llista_coms.get(j);
    //         Iterator<Integer> iter = llista.iterator();
    //         System.out.println("Adjacències en la Comunitat "+j+": "+grafPajek.adj_coms.get(j));
    //         System.out.println("Vèrtexs en la Comunitat "+j+":");
    //         while (iter.hasNext()) {
    //             Integer vert = iter.next();
    //             System.out.println(vert);
    //         }
    //         System.out.println();
    //     }
    // }

    public static void main(String[] args) {

        // --------------------------- RESULTATS DELS PROGRAMES (TEMPS D'EXECUCIÓ): ---------------------------
        // Paràmetres: num_iter, max_iter (fase 1) // num_iter, max_iter (fase 2)

        // HEPPhysics (27.770 vèrtexs):
        // 100.000, 10.000 // 15, 7 --> 159,9 segons.
        // 100.000, 10.000 // 10, 3 --> 125,2 segons.
        // 100.000, 10.000 // 1, 1 --> 100,7 segons.
        // *Aquests han estat els resultats mitjans, després d'una sèrie de 3 execucions amb cada configuració de paràmetres. 

        // PGPgiantcompo.net (10.680 vèrtexs):
        // 1.000.000, 100.000 // 100, 10 --> 75,6 segons.
        // 10.000.000, 1.000.000 // 50, 5 --> 31,3 segons.
        // 1.000.000, 100.000 // 10, 3 --> 19,4 segons.

        // netscience.net (1.589 vèrtexs):
        // 10.000.000, 1.000.000 // 1.000.000, 100.000 --> 60,0 segons.
        // 20.000.000, 2.000.000 // 5.000.000, 10.000 --> 9,0 segons.
        // 50.000.000, 5.000.000 // 10.000.000, 5.000 --> 8,5 segons.
        // 10.000.000, 1.000.000 // 10.000.000, 1.000 --> 2,0 segons.
        // *Aquest és més ràpid que el de sota, ja que té moltes menys arestes!

        // email.net (1.133 vèrtexs):
        // 10.000.000, 1.000.000 // 10.000.000, 1.000 --> 29,2 segons.
        // 1.000.000, 100.000 // 10.000, 1.000 --> 9,3 segons.
        // 100.000, 10.000 // 10.000, 1.000 --> 7,1 segons.
        // 100.000, 10.000 // 1.000, 100 --> 1,9 segons.

        // celegans_metabolic.net (453 vèrtexs):
        // 10.000.000, 1.000.000 // 100.000, 10.000 --> 21,4 segons.
        // 1.000.000, 100.000 // 100.000, 10.000 --> 10,0 segons.
        // 100.000, 10.000 // 10.000, 1.000 --> 1,7 segons.

        // jazz.net (198 vèrtexs):
        // 10.000.000, 1.000.000 // 100.000, 10.000 --> 28,1 segons.
        // 1.000.000, 1.00.000 // 10.000, 1.000 --> 3,38 segons.
        // 500.000, 50.000 // 1.000, 100 --> 1,5 segons.

        // Zack.net (34 vèrtexs):
        // 100.000.000, 10.000.000 // 1.000.000, 100.000 --> 12,3 segons.
        // 10.000.000, 1.000.000 // 100.000, 10.000 --> 1,7 segons.
        // 1.000.000, 100.000 // 10.000, 1.000 --> 0,4 segons.

        // 4k4.net (16 vèrtexs):
        // 100.000.000, 10.000.000 // 1.000.000, 100.000 --> 6,5 segons.
        // 10.000.000, 1.000.000 // 100.000, 10.000 --> 0,8 segons.
        // 1.000.000, 100.000 // 10.000, 1.000 --> 0,2 segons.

        // Nom de l'arxiu a carregar.
        String filePath = "jazz.net";
        // Mida inicial del graf que es vol desar (en cas que el graf a carregar tingui més vèrtexs que aquest nombre, es redimensiona el graf per a que hi cabin, així que no hi ha problema).
        int mida = 100;

        try {
            // Es compta el temps inicial (això serà per comptar el temps d'execució del programa).
            long startTime = System.nanoTime();
            // Es desa el graf de tipus GrafPajek.
            GrafPajek grafPajek = new GrafPajek(mida,filePath);

            // ---------------------------- COMPROVACIONS DEL GRAF: ----------------------------
            // GrafComprovacions(grafPajek);
            // imprimirComs(grafPajek);

            // ------------------------ COMPROVACIÓ DE LA MODULARITAT INICIAL: -------------------------
            double mod_inicial = grafPajek.CalcularModularitat(grafPajek);
            System.out.println("Modularitat inicial = "+mod_inicial+" (Donada una partició inicial aleatòria).");
            
            // ------------------------ VARIACIÓ DE COMUNITATS (LOCALMENT) <=> FASE 1: -------------------------
            System.out.println(" ----------------------  FASE 1: ---------------------- ");
            // ModificarModularitat(num_iter,max_iter,graf)
                // num_iter >>> Nombre d'iteracions a realitzar (nombre de cops que es vol canviar un vèrtex del graf de comunitat).
                // max_iter >>> Nombre màxim d'iteracions a realitzar sense que es canvii la modularitat (si es fan més d'aquestes iteracions sense que hagi augmentat la modularitat, el programa s'atura).
                //   graf   >>> Graf que conté tota la informació de la xarxa a analitzar.
            grafPajek.ModificarModularitat(1000000,100000,grafPajek);
            
            double mod_final1 = grafPajek.CalcularModularitat(grafPajek);
            System.out.println("Modularitat Fase 1 = "+mod_final1);
            System.out.println("Nombre de comunitats: "+grafPajek.ComunitatsNoBuides().numElements());

            // ------------------------ VARIACIÓ DE COMUNITATS (GLOBALMENT) <=> FASE 2: -------------------------
            System.out.println(" ----------------------  FASE 2: ---------------------- ");
            // // FusionarComunitats(num_iter,max_iter,graf)
            // num_iter >>> Nombre d'iteracions a realitzar (nombre de cops que es vol intentar fusionar totes les comunitats del graf entre elles).
            // max_iter >>> Nombre màxim d'iteracions a realitzar sense que es canvii la modularitat (si es fan més d'aquestes iteracions sense que hagi augmentat la modularitat, el programa s'atura).
            //   graf   >>> Graf que conté tota la informació de la xarxa a analitzar.
            grafPajek.FusionarComunitats(10000,1000,grafPajek);

            double mod_final2 = grafPajek.CalcularModularitat(grafPajek);
            System.out.println("Modularitat Fase 2 = "+mod_final2);
            System.out.println("Nombre de comunitats: "+grafPajek.ComunitatsNoBuides().numElements());
           
            // Es defineix el nom de l'arxiu a retornar (Primera paraula abans del punt del filePath + _modularitat_final + '.clu')
            String fileName = filePath.split("\\.")[0]+"_"+mod_final2+".clu";
            grafPajek.RetornarArxiu(fileName);

            long endTime = System.nanoTime();
            System.err.println("Temps d'execució: "+(float) (endTime-startTime)/1000000000+" segons.");

        } catch (IOException e) {
            System.out.println("Error al llegir l'arxiu: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
