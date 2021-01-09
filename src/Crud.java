import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.query.Predicate;
import com.db4o.query.Query;

import java.sql.Date;
import java.util.Scanner;

public class Crud {

    private ObjectContainer db = Db4oEmbedded.openFile(
            Db4oEmbedded.newConfiguration(),
            "escuela_alexis.db4o"
    );

    private boolean addData(Object data) {
        try {
            db.store(data);
            db.commit();
            return true;
        } catch (Exception e) {
            db.rollback();
            System.out.println("error to add data");
            System.err.println(e);
            return false;
        }

    }


    public void addStudent() {
        System.out.println("--Añadir un Alumno--");
        Alumno new_student = new Alumno(
                getString("Matricula:"),
                getString("Nombre:"),
                Date.valueOf(getString("fecha de nacimiento:"))
        );
        if (addData(new_student)) {
            System.out.println("alumno registrado correctamente");
        } else {
            System.out.println("Ocurrió un error al intentar registrarlo");
        }
    }


    public void findAllStudents() {
        try {
            Query query = db.query();
            query.constrain(Alumno.class);
            ObjectSet<Alumno> find_all = query.execute();
            for (Alumno a : find_all) {
                System.out.println(a);
            }

        } catch (Exception e) {
            System.out.println("error to find all students");
            System.err.println(e);
        }
    }


    public void findAllSubjects() {
        try {
            Query query = db.query();
            query.constrain(Materia.class);
            ObjectSet<Materia> find_all = query.execute();
            for (Materia m : find_all) {
                System.out.println(m);
            }

        } catch (Exception e) {
            System.out.println("error to find all subjects");
            System.err.println(e);
        }

    }


    private Object getUpdatedString(String msj, Object value) {
        String user_answer = null;
        while (user_answer == null) {
            user_answer = getString(msj);
            if (user_answer.toLowerCase() == "exit") {
                run();
            }
            if (user_answer != null) return value;
        }
        return user_answer;
    }


    public void updateStudent() {
        try {
            String user_answer = getString("matricula");
            ObjectSet<Alumno> result = db.query(new Predicate<Alumno>() {
                @Override
                public boolean match(Alumno alumno) {
                    System.out.println(2);
                    return alumno.getMatricula().contains(user_answer);
                }
            });
            for (Alumno a : result) {
                System.out.println(a);
            }
            Alumno updated_student = result.next();
            System.out.println(updated_student);
            updated_student.setNombre(
                    (String) getUpdatedString("nombre", updated_student.getNombre())
            );
            updated_student.setMatricula(
                    (String) getUpdatedString("matricula", updated_student.getNombre())
            );
            updated_student.setFecha_nacimiento(
                    (Date) getUpdatedString("fecha de nacimiento", updated_student.getFecha_nacimiento())
            );
            db.store(updated_student);
            db.commit();
        } catch (Exception e) {
            db.rollback();
            System.out.println("no esta registrada esa matricula");
        }

    }

    public String getString(String msj) {
        Scanner sc = new Scanner(System.in);
        try {
            System.out.print(msj);
            String request = sc.nextLine();

            if (request.equalsIgnoreCase("")) System.out.println("\'exit\' para regresar");
            else return request;
        } catch (Exception e) {
            System.out.println("ingresa un valor");
        }
        return getString(msj);
    }

    public int getInt(String msj) {
        Scanner sc = new Scanner(System.in);
        try {
            System.out.print(msj);
            return sc.nextInt();
        } catch (Exception e) {
            System.out.println("ingresa un número");
            return getInt(msj);
        }
    }

    public void studentsMenu() {
        while (true) {
            System.out.println("--menú de alumnos --" +
                    "\n1. Añadir" +
                    "\n2. Actualizar" +
                    "\n3. Buscar por matricula" +
                    "\n4. Buscar por coincidencia" +
                    "\n5. Listar todos" +
                    "\n5. Eliminar" +
                    "\n6. Regresar al menú principal");
            int user_answer = getInt("opción:");
            switch (user_answer) {
                case 1:
                    addStudent();
                    break;
                case 2:
                    updateStudent();
                    break;
                case 3:
                    addStudent();
                    break;
                case 4:
                    updateStudent();
                    break;
                case 5:
                    findAllStudents();
                    break;
                case 6:
                    System.out.println("volviendo...");
                    run();
                    break;
                default:
                    System.out.println("ingresa una respuesta valida");
            }

        }
    }

    public void subjetsMenu() {
        while (true) {
            System.out.println("--menú de materias --" +
                    "\n1. Añadir" +
                    "\n2. Actualizar" +
                    "\n3. Buscar por código" +
                    "\n4. Buscar por coincidencia" +
                    "\n5. Eliminar" +
                    "\n6. Regresar al menú principal");
            int user_answer = getInt("opción:");
        }
    }

    public void run() {
        System.out.println("Bienvido a SOA");
        while (true) {
            System.out.println("--menú principal--" +
                    "\n1. Alumnos" +
                    "\n2. Materias" +
                    "\n3. Salir");
            int user_answer = getInt("opción:");
            switch (user_answer) {
                case 1:
                    studentsMenu();
                    break;
                case 2:
                    subjetsMenu();
                    break;
                case 3:
                    System.out.println("Bye");
                    break;
                default:
                    System.out.println("ingresa una respuesta valida");
            }
            if (user_answer == 3) break;
        }
        db.close();
    }

    public static void main(String[] args) {
        Crud def = new Crud();
        def.run();
    }
}