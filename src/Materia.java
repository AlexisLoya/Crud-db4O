import java.io.Serializable;
import java.util.ArrayList;
import java.util.Set;

public class Materia implements Serializable {
    private String clave;
    private String nombre;
    private Set<Alumno> alumnos;

    public Materia(String clave, String nombre, Set<Alumno> alumnos) {
        this.clave = clave;
        this.nombre = nombre;
        this.alumnos = alumnos;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Set<Alumno> getAlumnos() {
        return alumnos;
    }

    public void setAlumnos(Set<Alumno> alumnos) {
        this.alumnos = alumnos;
    }

    public void addAlumno(Alumno newAlumno){
        alumnos.add(newAlumno);
    }

    public void deleteAlumno(Alumno oldAlumno){
        alumnos.remove(oldAlumno);
    }


    @Override
    public String toString() {
        return "Materia{" +
                "clave='" + clave + '\'' +
                ", nombre='" + nombre + '\'' +
                ", alumnos=" + alumnos +
                '}';
    }
}
