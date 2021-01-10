import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.query.Predicate;
import com.db4o.query.Query;

import java.sql.Date;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class Crud {

    private ObjectContainer db = Db4oEmbedded.openFile(
            Db4oEmbedded.newConfiguration(),
            "escuela_alexis.db4o"

    );
   // db.Ext().Crud().CascadeOnDelete(true);
   // db.Ext().Configure().ObjectClass(typeof(Circle)).CascadeOnDelete(true);


    protected boolean addData(Object data) {
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


    protected String checkMatricula(String msj) {
        try {
            String user_answer = getString("matricula:");
            ObjectSet<Alumno> result = db.query(new Predicate<Alumno>() {
                @Override
                public boolean match(Alumno alumno) {
                    return alumno.getMatricula().equalsIgnoreCase(user_answer);
                }
            });
            if (!result.isEmpty()) {
                System.out.println("esa matricula ya se encuentra en uso\nIntenta con otra");
                return checkMatricula(msj);
            } else {
                return user_answer;
            }
        } catch (Exception e) {
            return checkMatricula(msj);
        }
    }

    protected String checkCode(String msj) {
        try {
            String user_answer = getString("clave:");
            ObjectSet<Materia> result = db.query(new Predicate<Materia>() {
                @Override
                public boolean match(Materia materia) {
                    return materia.getClave().equalsIgnoreCase(user_answer);
                }
            });
            if (!result.isEmpty()) {
                System.out.println("ese clave ya se encuentra en uso\nIntenta con otro");
                return checkCode(msj);
            } else {
                return user_answer;
            }
        } catch (Exception e) {
            return checkCode(msj);
        }
    }

    public void addStudent() {
        System.out.println("--Añadir un Alumno--");
        Alumno new_student = new Alumno(
                checkMatricula("Matricula:"),
                getString("Nombre:"),
                getString("fecha de nacimiento:")
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


    public Alumno findStudentForMatricula() {
        Alumno alumno = null;
        try {
            String user_answer = getString("matricula:");
            ObjectSet<Alumno> result = db.query(new Predicate<Alumno>() {
                @Override
                public boolean match(Alumno alumno) {
                    return alumno.getMatricula().equalsIgnoreCase(user_answer);
                }
            });
            alumno = result.next();
        } catch (Exception e) {
            System.out.println("Ese alumno no esta registrado");
        }
        return alumno;
    }

    public void findStudentForAny() {
        try {
            String user_answer = getString("nombre:");
            ObjectSet<Alumno> result = db.query(new Predicate<Alumno>() {
                @Override
                public boolean match(Alumno alumno) {
                    return alumno.getNombre().contains(user_answer);
                }
            });
            for (Alumno a : result) {
                System.out.println(a);
            }
        } catch (Exception e) {
            System.out.println("No se encontró ningúna coincidencia");
        }
    }





    public void updateStudent() {
        try {
            Alumno updated_student = findStudentForMatricula();
            System.out.println(updated_student);
            System.out.println("matricula:"+updated_student.getMatricula());
            updated_student.setNombre(
                    (String) getUpdatedString("nombre:", updated_student.getNombre())
            );
            updated_student.setFecha_nacimiento(
                    (String) getUpdatedString("fecha de nacimiento:", updated_student.getFecha_nacimiento())
            );
            db.store(updated_student);
            db.commit();
        } catch (Exception e) {
            db.rollback();
            System.out.println(e.getMessage());
            System.out.println("no esta registrada esa matricula");
        }

    }

    private Object getUpdatedString(String msj, Object value) {
        String user_answer = null;
        user_answer = getString(msj);
        if (user_answer.equalsIgnoreCase("")) return value;
        return user_answer;
    }

    public void deleteStudent() {
        try {
            Alumno deleted_student = findStudentForMatricula();
            if (deleted_student == null) {
                return;
            } else {
                System.out.println("¿Estás segúro de eliminar al estudiante?\n1. si\n2. cancelar");
                int user_answer = getInt("opción:");
                switch (user_answer) {
                    case 1:
                        ObjectSet<Materia> result = db.query(new Predicate<Materia>() {
                            @Override
                            public boolean match(Materia materia) {
                                return materia != null;
                            }
                        });
                        while (result.hasNext()){
                            Materia materia = result.next();
                            Set<Alumno> updated_list = materia.getAlumnos();
                            for (Alumno a: updated_list) {
                                if (a.getMatricula().equals(deleted_student.getMatricula())){
                                    updated_list.remove(deleted_student);
                                    break;
                                }
                            }
                            materia.setAlumnos(updated_list);
                            db.store(materia);
                        }
                        ObjectSet<Alumno> result_1 = db.query(new Predicate<Alumno>() {
                            @Override
                            public boolean match(Alumno alumno) {
                                return alumno.getMatricula().equalsIgnoreCase(deleted_student.getMatricula());
                            }
                        });
                        Alumno alumno = result_1.next();
                        db.delete(alumno);
                        db.commit();
                        System.out.println("Alumno Eliminado correctamente");
                        break;
                    case 2:
                        System.out.println("cancelando eliminación...");
                        break;
                    default:
                        System.out.println("Escoge una opción disponible");
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void addSSubject() {
        System.out.println("--Añadir una Materia--");
        Set<Alumno> alumnos = new HashSet<>();
        Materia new_subject = new Materia(
                checkCode("Matricula:"),
                getString("Nombre:"),
                alumnos
        );
        if (addData(new_subject)) {
            System.out.println("materia registrada correctamente");
        } else {
            System.out.println("Ocurrió un error al intentar registrarla");
        }
    }

    public void findSubjectForAny() {
        try {
            String user_answer = getString("nombre:");
            ObjectSet<Materia> result = db.query(new Predicate<Materia>() {
                @Override
                public boolean match(Materia materia) {
                    return materia.getNombre().contains(user_answer);
                }
            });
            System.out.println(1);
            for (Materia a : result) {
                System.out.println(a);
            }
            System.out.println(2);
        } catch (Exception e) {
            System.out.println("No se encontró ningúna coincidencia");
        }
    }

    public Materia findSubjectForCode() {
        Materia materia = null;
        try {
            String user_answer = getString("clave:");
            ObjectSet<Materia> result = db.query(new Predicate<Materia>() {
                @Override
                public boolean match(Materia materia) {
                    return materia.getClave().equalsIgnoreCase(user_answer);
                }
            });
            materia = result.next();
        } catch (Exception e) {
            System.out.println("Esa materia no esta registrada");
        }
        return materia;
    }


    public void deleteSubject() {
        try {
            Materia deleted_subject = findSubjectForCode();
            if (deleted_subject == null) {
                return;
            } else {
                System.out.println("¿Estás segúro de eliminar la materia?\n1. si\n2. cancelar");
                int user_answer = getInt("opción:");
                switch (user_answer) {
                    case 1:
                        Query query = db.query();
                        query.constrain(Materia.class);
                        query.descend("clave").constrain(deleted_subject.getClave());
                        ObjectSet<Materia> result = query.execute();
                        Materia materia = result.next();
                        db.delete(materia);
                        db.commit();
                        System.out.println("Materia Eliminada correctamente");
                        break;
                    case 2:
                        System.out.println("cancelando eliminación...");
                        break;
                    default:
                        System.out.println("Escoge una opción disponible");
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public Alumno findStudentInSubject(String nombre){
        Alumno student = findStudentForMatricula();
        Alumno finded_student = null;
        try {
            Set<Alumno> alumnos = new HashSet<>();
            ObjectSet<Materia> result = db.query(new Predicate<Materia>() {
                @Override
                public boolean match(Materia materia) {
                    return materia.getNombre().equalsIgnoreCase(nombre);
                }
            });
            alumnos = result.next().getAlumnos();
            for (Alumno a: alumnos) {
                if(a.getMatricula() == student.getMatricula()){
                    finded_student = student;
                }
            }
        } catch (Exception e) {
            System.out.println("Ese alumno no esta registrado en la materia");
        }
        return finded_student;
    }

    public void addStudentToSubject() {
        System.out.println("--Añadir Alumno a una materia--");
        Materia materia = findSubjectForCode();
        if (materia == null) return;
        Alumno alumno = findStudentForMatricula();
        if (alumno == null) return;
        System.out.println(alumno);
        System.out.println("Clave:"+materia.getClave()+
                "\nMateria:"+materia.getNombre()+
                "\n ¿Estás seguro de agregar al alumno?\n1.Si\n2.Cancelar");
        int user_answer = getInt("opción:");
        switch (user_answer) {
            case 1:
                Query query = db.query();
                query.constrain(Materia.class);
                query.descend("clave").constrain(materia.getClave());
                ObjectSet<Materia> result = query.execute();
                materia = result.next();
                //add
                if (materia.getAlumnos().isEmpty()){
                    Set<Alumno> alumnos = new HashSet<>();
                    alumnos.add(alumno);
                    materia.setAlumnos(alumnos);
                }else{
                    Set<Alumno> alumnos = new HashSet<>();
                    for (Alumno a:materia.getAlumnos()) {
                        alumnos.add(a);
                    }
                    alumnos.add(alumno);
                    materia.setAlumnos(alumnos);
                }
                db.store(materia);
                db.commit();
                System.out.println("Alumno agregado correctamente");
                break;
            case 2:
                System.out.println("cancelando...");
                break;
            default:
                System.out.println("Escoge una opción disponible");
        }

    }


    public void deleteStudentToSubject() {
        System.out.println("--Eliminar Alumno de una materia--");
        Materia materia = findSubjectForCode();
        if (materia == null) return;
        Alumno alumno = findStudentInSubject(materia.getClave());
        if (alumno == null) return;
        System.out.println(alumno);
        System.out.println("Clave:"+materia.getClave()+
                "\nMateria:"+materia.getNombre()+
                "\n ¿Estás seguro de eliminar al alumno?\n1.Si\n2.Cancelar");
        int user_answer = getInt("opción:");
        switch (user_answer) {
            case 1:
                Query query = db.query();
                query.constrain(Materia.class);
                query.descend("clave").constrain(materia.getClave());
                ObjectSet<Materia> result = query.execute();
                materia = result.next();
                //add
                    Set<Alumno> alumnos = new HashSet<>();
                    for (Alumno a:materia.getAlumnos()) {
                        alumnos.add(a);
                    }
                    alumnos.remove(alumno);
                    materia.setAlumnos(alumnos);

                //materia.deleteAlumno(alumno);
                db.store(materia);
                db.commit();
                System.out.println("Alumno eliminado correctamente");
                break;
            case 2:
                System.out.println("cancelando...");
                break;
            default:
                System.out.println("Escoge una opción disponible");
        }

    }

    public void updateSubject() {
        try {
            Materia updated_subject = findSubjectForCode();
            System.out.println("Clave:"+updated_subject.getClave()+
                    "\nNombre:"+updated_subject.getNombre()+
                    "\nAlumnos:"+updated_subject.getAlumnos().size());
            updated_subject.setNombre(
                    (String) getUpdatedString("nombre:", updated_subject.getNombre())
            );

            db.store(updated_subject);
            db.commit();
        } catch (Exception e) {
            db.rollback();
            System.out.println(e.getMessage());
            System.out.println("no esta registrada esa matricula");
        }

    }


    public void subjetsMenu() {
        while (true) {
            System.out.println("--menú de materias--" +
                    "\n1. Añadir" +
                    "\n2. Actualizar" +
                    "\n3. Añadir alumno a una materia" +
                    "\n4. Eliminar alumno de una materia" +
                    "\n5. Buscar por clave" +
                    "\n6. Buscar por coincidencia" +
                    "\n7. Listar todas" +
                    "\n8. Eliminar" +
                    "\n9. Regresar al menú principal");
            int user_answer = getInt("opción:");
            switch (user_answer) {
                case 1:
                    addSSubject();
                    break;
                case 2:
                    updateSubject();
                    break;
                case 3:
                    addStudentToSubject();
                    break;
                case 4:
                    deleteStudentToSubject();
                    break;
                case 5:
                    Materia materia = findSubjectForCode();
                    if (materia != null) System.out.println(materia);
                    break;
                case 6:
                    findSubjectForAny();
                    break;
                case 7:
                    findAllSubjects();
                    break;
                case 8:
                    deleteSubject();
                    break;
                case 9:
                    System.out.println("volviendo...");
                    break;
                default:
                    System.out.println("ingresa una respuesta valida");
            }
            if (user_answer == 9) break;
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


    protected String getString(String msj) {
        Scanner sc = new Scanner(System.in);
        try {
            System.out.print(msj);
            String request = sc.nextLine();

            return request;
        } catch (Exception e) {
            System.out.println("ingresa un valor");
        }
        return getString(msj);
    }

    protected int getInt(String msj) {
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
                    "\n6. Eliminar" +
                    "\n7. Regresar al menú principal");
            int user_answer = getInt("opción:");
            switch (user_answer) {
                case 1:
                    addStudent();
                    break;
                case 2:
                    updateStudent();
                    break;
                case 3:
                    Alumno alumno = findStudentForMatricula();
                    if (alumno != null) System.out.println(alumno);
                    break;
                case 4:
                    findStudentForAny();
                    break;
                case 5:
                    findAllStudents();
                    break;
                case 6:
                    deleteStudent();
                    break;
                case 7:
                    System.out.println("volviendo...");
                    break;
                default:
                    System.out.println("ingresa una respuesta valida");
            }
            if (user_answer == 7) break;
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
