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
                public boolean match(Materia subject) {
                    return subject.getClave().equalsIgnoreCase(user_answer);
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
        try {
            db.store(new_student);
            db.commit();
            System.out.println("alumno registrado correctamente");
        } catch (Exception e) {
            db.rollback();
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
        Alumno student = null;
        try {
            String user_answer = getString("matricula:");
            ObjectSet<Alumno> result = db.query(new Predicate<Alumno>() {
                @Override
                public boolean match(Alumno alumno) {
                    return alumno.getMatricula().equalsIgnoreCase(user_answer);
                }
            });
            student = result.next();
        } catch (Exception e) {
            System.out.println("Ese alumno no esta registrado");
        }
        return student;
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
            System.out.println("matricula:" + updated_student.getMatricula());
            updated_student.setNombre(getString("nombre:"));
            updated_student.setFecha_nacimiento(getString("fecha de nacimiento:"));
            db.store(updated_student);
            db.commit();
        } catch (Exception e) {
            db.rollback();
            System.out.println(e.getMessage());
            System.out.println("no esta registrada esa matricula");
        }

    }


    public void deleteStudent() {

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
                        public boolean match(Materia subject) {
                            return subject != null;
                        }
                    });
                    try {
                        while (result.hasNext()) {
                            Materia subject = result.next();
                            Set<Alumno> updated_list = subject.getAlumnos();
                            for (Alumno a : updated_list) {
                                if (a.getMatricula().equals(deleted_student.getMatricula())) {
                                    updated_list.remove(deleted_student);
                                    break;
                                }
                            }
                            subject.setAlumnos(updated_list);
                            db.store(subject);
                        }
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    ObjectSet<Alumno> result_1 = db.query(new Predicate<Alumno>() {
                        @Override
                        public boolean match(Alumno alumno) {
                            return alumno.getMatricula().equalsIgnoreCase(deleted_student.getMatricula());
                        }
                    });
                    Alumno student = result_1.next();
                    db.delete(student);
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
    }

    public void addSSubject() {
        System.out.println("--Añadir una Materia--");
        Set<Alumno> alumnos = new HashSet<>();
        Materia new_subject = new Materia(
                checkCode("Matricula:"),
                getString("Nombre:"),
                alumnos
        );
        try {
            db.store(new_subject);
            db.commit();
            System.out.println("materia registrada correctamente");
        } catch (Exception e) {
            db.rollback();
            System.out.println("Ocurrió un error al intentar registrarla");
        }
    }

    public void findSubjectForAny() {
        try {
            String user_answer = getString("nombre:");
            ObjectSet<Materia> result = db.query(new Predicate<Materia>() {
                @Override
                public boolean match(Materia subject) {
                    return subject.getNombre().contains(user_answer);
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
        Materia subject = null;
        try {
            String user_answer = getString("clave:");
            ObjectSet<Materia> result = db.query(new Predicate<Materia>() {
                @Override
                public boolean match(Materia subject) {
                    return subject.getClave().equalsIgnoreCase(user_answer);
                }
            });
            subject = result.next();
        } catch (Exception e) {
            System.out.println("Esa materia no esta registrada");
        }
        return subject;
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
                        Materia subject = result.next();
                        db.delete(subject);
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

    public Alumno findStudentInSubject(String nombre) {
        Alumno student = findStudentForMatricula();
        Alumno finded_student = null;
        try {
            Set<Alumno> alumnos = new HashSet<>();
            ObjectSet<Materia> result = db.query(new Predicate<Materia>() {
                @Override
                public boolean match(Materia subject) {
                    return subject.getNombre().equalsIgnoreCase(nombre);
                }
            });
            alumnos = result.next().getAlumnos();
            for (Alumno a : alumnos) {
                if (a.getMatricula().equals(student.getMatricula())) {
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
        Materia subject = findSubjectForCode();
        if (subject == null) {
            return;
        }
        Alumno student = findStudentForMatricula();
        if (student == null) {
            return;
        }
        System.out.println(student);
        System.out.println("Clave:" + subject.getClave() +
                "\nMateria:" + subject.getNombre() +
                "\n ¿Estás seguro de agregar al alumno?\n1.Si\n2.Cancelar");
        int user_answer = getInt("opción:");
        switch (user_answer) {
            case 1:
                Query query = db.query();
                query.constrain(Materia.class);
                query.descend("clave").constrain(subject.getClave());
                ObjectSet<Materia> result = query.execute();
                subject = result.next();
                //add
                if (subject.getAlumnos().isEmpty()) {
                    Set<Alumno> alumnos = new HashSet<>();
                    alumnos.add(student);
                    subject.setAlumnos(alumnos);
                } else {
                    Set<Alumno> alumnos = new HashSet<>();
                    for (Alumno a : subject.getAlumnos()) {
                        alumnos.add(a);
                    }
                    alumnos.add(student);
                    subject.setAlumnos(alumnos);
                }
                db.store(subject);
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
        Materia subject = findSubjectForCode();
        if (subject == null) {
            return;
        }
        Alumno student = findStudentInSubject(subject.getClave());
        if (student == null) {
            return;
        }
        System.out.println(student);
        System.out.println("Clave:" + subject.getClave() +
                "\nMateria:" + subject.getNombre() +
                "\n ¿Estás seguro de eliminar al alumno?\n1.Si\n2.Cancelar");
        int user_answer = getInt("opción:");
        switch (user_answer) {
            case 1:
                Query query = db.query();
                query.constrain(Materia.class);
                query.descend("clave").constrain(subject.getClave());
                ObjectSet<Materia> result = query.execute();
                subject = result.next();
                //add
                Set<Alumno> alumnos = new HashSet<>();
                for (Alumno a : subject.getAlumnos()) {
                    alumnos.add(a);
                }
                alumnos.remove(student);
                subject.setAlumnos(alumnos);

                //materia.deleteAlumno(alumno);
                db.store(subject);
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
            System.out.println("Clave:" + updated_subject.getClave() +
                    "\nNombre:" + updated_subject.getNombre() +
                    "\nAlumnos:" + updated_subject.getAlumnos().size());
            updated_subject.setNombre(getString("nombre:"));
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
                    Materia subject = findSubjectForCode();
                    if (subject != null) {
                        System.out.println(subject);
                    }
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
            if (user_answer == 9) {
                break;
            }
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
                    Alumno student = findStudentForMatricula();
                    if (student != null) {
                        System.out.println(student);
                    }
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
            if (user_answer == 7) {
                break;
            }
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
            if (user_answer == 3) {
                break;
            }
        }
        db.close();
    }

    public static void main(String[] args) {
        Crud def = new Crud();
        def.run();
    }
}
