import java.io.Serializable;
import java.util.Date;

public class Alumno implements Serializable {
    private String matricula;
    private String nombre;
    private String fecha_nacimiento;

    public Alumno(String matricula, String nombre, String fecha_nacimiento) {
        this.matricula = matricula;
        this.nombre = nombre;
        this.fecha_nacimiento = fecha_nacimiento;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getFecha_nacimiento() {
        return fecha_nacimiento;
    }

    public void setFecha_nacimiento(String fecha_nacimiento) {
        this.fecha_nacimiento = fecha_nacimiento;
    }

    @Override
    public String toString() {
        return "Alumno{" +
                "matricula='" + matricula + '\'' +
                ", nombre='" + nombre + '\'' +
                ", fecha_nacimiento='" + fecha_nacimiento + '\'' +
                '}';
    }
}
