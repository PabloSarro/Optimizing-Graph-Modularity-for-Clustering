package cat.urv.deim;

import java.io.IOException;

// The next imports are used if the two initial auxiliary methods are uncommented.

// import java.util.Iterator;
// import cat.urv.deim.exceptions.PosicioForaRang;


// --------------------------------------------------------------------------------------------------------------------------------
// ----------------------------------- EXPLANATION OF THIS PROGRAM IS FOUND AT THE README FILE ------------------------------------
// --------------------------------------------------------------------------------------------------------------------------------

public class Main {

    // ---------------- Auxiliary mathod to check if the graph has been read correctly: --------------------------------

    // public static void GrafComprovacions(GrafPajek grafPajek) {
    //     try {
    //         System.out.println("---------------GRAPH CHECKING:---------------");
    //         System.out.println("Number of vertices: " + grafPajek.numVertexs());
    //         System.out.println("Number of edges: " + grafPajek.numArestes());
    //         System.out.print("Vertex list: ");
    //         ILlistaGenerica<Integer> personesIDs = grafPajek.obtenirVertexIDs();
    //         for (int i = 0; i < personesIDs.numElements(); i++) {
    //             System.out.print(personesIDs.consultar(i) + " ");
    //         }
    //     } catch (PosicioForaRang e) {
    //         System.out.println("Exception PosicioForaRang in the method GrafComprovacions");
    //     }
    // }

    
    // ---------------- Auxiliary method to check the vertex list for each graph community: ----------------

    // public static void imprimirComs(GrafPajek grafPajek) {
    //     System.out.println();
    //     for (int j = 0; j < grafPajek.llista_coms.size(); j++) {
    //         LlistaNoOrdenada<Integer> llista = grafPajek.llista_coms.get(j);
    //         Iterator<Integer> iter = llista.iterator();
    //         System.out.println("Adjacencies in the Community "+j+": "+grafPajek.adj_coms.get(j));
    //         System.out.println("Vertices in Community "+j+":");
    //         while (iter.hasNext()) {
    //             Integer vert = iter.next();
    //             System.out.println(vert);
    //         }
    //         System.out.println();
    //     }
    // }

    public static void main(String[] args) {

        // --------------------------- EXAMPLE EXECUTION TIMES: ---------------------------
        // Parameters: num_iter, max_iter (phase 1) // num_iter, max_iter (phase 2)

        // These have been the average results after 3 execution with each parameter configuration:

        // HEPPhysics (27.770 vertices):
        // 100.000, 10.000 // 15, 7 --> 159,9 sec.
        // 100.000, 10.000 // 10, 3 --> 125,2 sec.
        // 100.000, 10.000 // 1, 1 --> 100,7 sec.

        // PGPgiantcompo.net (10.680 vertices):
        // 1.000.000, 100.000 // 100, 10 --> 75,6 sec.
        // 10.000.000, 1.000.000 // 50, 5 --> 31,3 sec.
        // 1.000.000, 100.000 // 10, 3 --> 19,4 sec.

        // netscience.net (1.589 vertices):
        // 10.000.000, 1.000.000 // 1.000.000, 100.000 --> 60,0 sec.
        // 20.000.000, 2.000.000 // 5.000.000, 10.000 --> 9,0 sec.
        // 50.000.000, 5.000.000 // 10.000.000, 5.000 --> 8,5 sec.
        // 10.000.000, 1.000.000 // 10.000.000, 1.000 --> 2,0 sec.

        // email.net (1.133 vertices):
        // 10.000.000, 1.000.000 // 10.000.000, 1.000 --> 29,2 sec.
        // 1.000.000, 100.000 // 10.000, 1.000 --> 9,3 sec.
        // 100.000, 10.000 // 10.000, 1.000 --> 7,1 sec.
        // 100.000, 10.000 // 1.000, 100 --> 1,9 sec.

        // celegans_metabolic.net (453 vertices):
        // 10.000.000, 1.000.000 // 100.000, 10.000 --> 21,4 sec.
        // 1.000.000, 100.000 // 100.000, 10.000 --> 10,0 sec.
        // 100.000, 10.000 // 10.000, 1.000 --> 1,7 sec.

        // jazz.net (198 vertices):
        // 10.000.000, 1.000.000 // 100.000, 10.000 --> 28,1 sec.
        // 1.000.000, 1.00.000 // 10.000, 1.000 --> 3,38 sec.
        // 500.000, 50.000 // 1.000, 100 --> 1,5 sec.

        // Zack.net (34 vertices):
        // 100.000.000, 10.000.000 // 1.000.000, 100.000 --> 12,3 sec.
        // 10.000.000, 1.000.000 // 100.000, 10.000 --> 1,7 sec.
        // 1.000.000, 100.000 // 10.000, 1.000 --> 0,4 sec.

        // 4k4.net (16 vertices):
        // 100.000.000, 10.000.000 // 1.000.000, 100.000 --> 6,5 sec.
        // 10.000.000, 1.000.000 // 100.000, 10.000 --> 0,8 sec.
        // 1.000.000, 100.000 // 10.000, 1.000 --> 0,2 sec.

        // Name of the graph file to load.
        String filePath = "jazz.net";
        // Initial size of the desired graph (in case the graph has more nodes than these, the graph is redimensioned, so there's no problem).
        int mida = 100;

        try {
            long startTime = System.nanoTime();
            // The graph is saved as type 'GrafPajek'.
            GrafPajek grafPajek = new GrafPajek(mida,filePath);

            // ---------------------------- GRAPH AUXILIARY CHECKINGS: ----------------------------
            // GrafComprovacions(grafPajek);
            // imprimirComs(grafPajek);

            // --------------------------- INITIAL MODULARITY CHECKING: ---------------------------
            double mod_inicial = grafPajek.CalcularModularitat(grafPajek);
            System.out.println("Initial modularity = "+mod_inicial+" (Given a random initial partition).");
            

            // ---------------------- LOCAL COMMUNITY VARIATION <=> PHASE 1: -----------------------
            System.out.println("\n ----------------------  PHASE 1: ---------------------- ");
            // ModificarModularitat(num_iter,max_iter,graf)
                // num_iter >>> Number of iterations to perform (number of times that a vertex community change is attempted).
                // max_iter >>> Maximum number of iterations to perform after no change in the modularity.
                //   graf   >>> Graph containing all the network information.
            
            int num_iter1 = 1000000;
            int max_iter1 = 100000;
            System.out.println("Chosen parameters: num_iter = "+num_iter1+", max_iter = "+max_iter1);
            grafPajek.ModificarModularitat(num_iter1,max_iter1,grafPajek);
            
            double mod_final1 = grafPajek.CalcularModularitat(grafPajek);
            System.out.println("Modularity after Phase 1 = "+mod_final1);
            System.out.println("Number of communities: "+grafPajek.ComunitatsNoBuides().numElements());


            // ------------------------ GLOBAL COMMUNITY VARIATION <=> PHASE 2: -------------------------
            System.out.println("\n ----------------------  PHASE 2: ---------------------- ");
            // // FusionarComunitats(num_iter,max_iter,graf)
            // num_iter >>> Number of iterations to perform (number of times that a vertex community change is attempted).
            // max_iter >>> Maximum number of iterations to perform after no change in the modularity.
            //   graf   >>> Graph containing all the network information.

            int num_iter2 = 10000;
            int max_iter2 = 1000;
            System.out.println("Chosen parameters: num_iter = "+num_iter2+", max_iter = "+max_iter2);
            grafPajek.FusionarComunitats(num_iter2,max_iter2,grafPajek);

            double mod_final2 = grafPajek.CalcularModularitat(grafPajek);
            System.out.println("Modularity after Phase 2 = "+mod_final2);
            System.out.println("Number of communities: "+grafPajek.ComunitatsNoBuides().numElements());
           
            // The new name of the file to return is defined.
            String fileName = filePath.split("\\.")[0]+"_"+mod_final2+".clu";
            grafPajek.RetornarArxiu(fileName);

            long endTime = System.nanoTime();
            System.err.println("Execution time: "+(float) (endTime-startTime)/1000000000+" seconds.");

        } catch (IOException e) {
            System.out.println("Error when trying to read the file: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
