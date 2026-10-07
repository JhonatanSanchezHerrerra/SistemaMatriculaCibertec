# Sistema de Registro y Matricula de Alumnos

Proyecto Java para Eclipse del curso Algoritmos y Estructura de Datos.

## Como abrirlo

1. Descomprimir el archivo ZIP.
2. En Eclipse, elegir `File > Import > Existing Projects into Workspace`.
3. Seleccionar la carpeta `SistemaMatriculaCibertec`.
4. Ejecutar `pe.cibertec.matricula.Principal` como Java Application.

## Estructura del proyecto

- `modelo`: clases Alumno, Curso, Matricula y Retiro.
- `servicio`: reglas de negocio, listas `ArrayList` y persistencia en archivos `.dat`.
- `ui`: interfaz grafica Swing organizada en Mantenimiento, Registro, Consulta y Reportes.
- `data`: se crea automaticamente al ejecutar y conserva los registros para la siguiente ejecucion.

## Funciones implementadas

- Mantenimiento completo de alumnos y cursos.
- Matricula, cambio de curso, cancelacion de matricula, retiro y cancelacion de retiro.
- Consultas por codigo o numero de operacion.
- Reportes de alumnos pendientes, matricula vigente y alumnos matriculados por curso.
- Validaciones de DNI unico, codigos unicos, estados del alumno y confirmacion antes de eliminar o cancelar.


## Plan de clases

| Clase | Responsabilidad |
|---|---|
| Alumno | Datos personales y estado: registrado, matriculado o retirado. |
| Curso | Datos academicos del curso y ordenamiento por codigo. |
| Matricula | Relacion entre un alumno y un curso con fecha y hora. |
| Retiro | Registro de la desactivacion temporal de una matricula. |
| Repositorio | Lectura y grabacion de las listas en archivos. |
| SistemaMatricula | Reglas de negocio y operaciones de los modulos. |
| VentanaPrincipal | Interfaz grafica con menus por modulo. |
| Principal | Punto de inicio de la aplicacion. |
