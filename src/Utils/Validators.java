/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Utils;

import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.regex.Pattern;

/**
 *
 * @author Luisk
 */
public class Validators {
    private Validators(){}

  // --- Campos obligatorios ---
  public static void noVacio(String s, String campo) throws ReglaDeNegocioException{
    if (s == null || s.trim().isEmpty())
      throw new ReglaDeNegocioException("El campo '" + campo + "' es obligatorio.");
  }

  // --- Email ---
  private static final Pattern EMAIL_RE = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
  public static void email(String s) throws ReglaDeNegocioException{
    noVacio(s, "correo");
    if (!EMAIL_RE.matcher(s).matches())
      throw new ReglaDeNegocioException("Correo con formato inválido.");
  }

  // --- Teléfono ---
  public static void telefono(String s, int largo) throws ReglaDeNegocioException {
    noVacio(s, "teléfono");
    String clean = s.trim().replaceAll("\\s+", ""); // quita espacios
    if (clean.length() != largo || !clean.chars().allMatch(Character::isDigit)) {
        throw new ReglaDeNegocioException("Teléfono debe tener " + largo + " dígitos numéricos.");
    }
}

  // --- Mayor de edad ---
  public static void mayorDeEdad(LocalDate fechaNac) throws ReglaDeNegocioException{
    if (fechaNac == null) throw new ReglaDeNegocioException("Fecha de nacimiento es obligatoria.");
    int edad = Period.between(fechaNac, LocalDate.now()).getYears();
    if (edad < 18) throw new ReglaDeNegocioException("Debe ser mayor de 18 años (actual: " + edad + ").");
  }

  // --- Año de vehículo / antigüedad ---
  public static void anioVehiculoValido(int anio, int maxAntig) throws ReglaDeNegocioException{
    int actual = LocalDate.now().getYear();
    if (anio > actual) throw new ReglaDeNegocioException("El año del vehículo no puede ser mayor al año actual.");
    if ((actual - anio) > maxAntig) throw new ReglaDeNegocioException("El vehículo supera la antigüedad máxima de " + maxAntig + " años.");
  }

  // --- Fechas reservas/contratos ---
  public static void inicioNoAntesDeHoy(LocalDate inicio) throws ReglaDeNegocioException{
    if (inicio == null) throw new ReglaDeNegocioException("Fecha de inicio obligatoria.");
    if (inicio.isBefore(LocalDate.now()))
      throw new ReglaDeNegocioException("La fecha de inicio no puede ser menor a hoy.");
  }

  public static void finPosterior(LocalDate inicio, LocalDate fin) throws ReglaDeNegocioException{
    if (fin == null) throw new ReglaDeNegocioException("Fecha de fin obligatoria.");
    if (!fin.isAfter(inicio))
      throw new ReglaDeNegocioException("La fecha de fin debe ser posterior a la de inicio.");
  }

  public static void duracionMaxima(LocalDate inicio, LocalDate fin, int maxDias) throws ReglaDeNegocioException{
    long dias = ChronoUnit.DAYS.between(inicio, fin);
    if (dias > maxDias)
      throw new ReglaDeNegocioException("La duración no puede exceder " + maxDias + " días (actual: " + dias + ").");
  }
}
