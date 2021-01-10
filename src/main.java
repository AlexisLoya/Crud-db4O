import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Set;

public class main {
    /*
    public static Date run(String user_date) {
        Date date1= null;
        try {
            SimpleDateFormat formatter1=new SimpleDateFormat("yyyy-MM-dd");
            date1=formatter1.parse(user_date);
            System.out.println(date1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date1;

    }

     */

    public static void main(String[] args) {
        Alumno alumno1 = new Alumno("20193tn151", "Alexis", ("2001-06-27"));
        Alumno alumno2 = new Alumno("20193tn142", "Pedro", ("2001-06-27"));
        Alumno alumno3 = new Alumno("20193tn153", "Juan", ("2001-06-27"));
        Alumno alumno4 = new Alumno("20193tn154", "Agustín", ("2001-06-27"));
        Alumno alumno5 = new Alumno("20193tn155", "Manuel", ("2001-06-27"));
        Alumno alumno6 = new Alumno("20193tn156", "Luis", ("2001-06-27"));
        Alumno alumno7 = new Alumno("20193tn157", "Raúl", ("2001-06-27"));
        Alumno alumno8 = new Alumno("20193tn158", "Ramon", ("2001-06-27"));
        Alumno alumno9 = new Alumno("20193tn158", "Hector", ("2001-06-27"));
        Alumno alumno10 = new Alumno("20193tn158", "Mane", ("2001-06-27"));

        Set<Alumno> alumnos = new HashSet<>();
        Set<Alumno> alumnos2 = new HashSet<>();
        Set<Alumno> alumnos3 = new HashSet<>();
        Materia materia1 = new Materia("TOE-3", "Taller de lectura y redacción", alumnos);
        System.out.println(materia1);
        System.out.printf("------------------------\n");
        Materia materia2 = new Materia("BD-5", "base de datos", alumnos2);
        System.out.println(materia1);
        System.out.printf("------------------------\n");
        Materia materia3 = new Materia("IG-5", "Ingles V", alumnos3);
        System.out.println(materia1);
        System.out.printf("------------------------\n");

        //db in Db4o
        ObjectContainer db = Db4oEmbedded.openFile(
                Db4oEmbedded.newConfiguration(),
                "escuela_alexis.db4o"
        );
        //add in db
        db.store(materia1);
        db.store(materia2);
        db.store(materia3);
        db.commit();

        db.store(alumno1);
        db.store(alumno2);
        db.store(alumno3);
        db.store(alumno4);
        db.store(alumno5);
        db.store(alumno6);
        db.store(alumno7);
        db.store(alumno8);
        db.store(alumno9);
        db.store(alumno10);
        //confirm data
        db.commit();
        //db.rollback();
        db.close();


        //db in Db4o

       /*ObjectSet find_all = db.queryByExample(
               new Alumno("20193tn151",null,null));

       for (Object a : find_all){
           System.out.println(a);
       }

        */
        /*
        Query query = db.query();
        query.constrain(Alumno.class);
        ObjectSet<Alumno> find_all = query.execute();
        for (Alumno a : find_all) {
            System.out.println(a);
        }
         */
        //db in Db4o
        /*
        ObjectContainer db = Db4oEmbedded.openFile(
                Db4oEmbedded.newConfiguration(),
                "escuela_alexis.db4o"
        );
        ObjectSet<Alumno> result = db.query(new Predicate<Alumno>() {
            @Override
            public boolean match(Alumno alumno) {
                return alumno.getNombre().contains("A") || alumno.getNombre().contains("M");
            }
        });
        for (Alumno a : result) {
            System.out.println(a);
        }



        //SODA - SIMPLE OBJECT DATABASE ACCESS
        Query query = db.query();
        query.constrain(Materia.class);
        query.descend("alumnos").descend("nombre").constrain("A");


        Query query1 = db.query();
        query1.constrain(Materia.class);
        query1.descend("nombre").constrain("base de datos");

        ObjectSet<Materia> resultado = query.execute();
        Materia materia = resultado.next();
        System.out.println(materia);
        materia.setNombre("WEB");
        db.store(materia);
        db.commit();
        db.close();

         */
    }
}
