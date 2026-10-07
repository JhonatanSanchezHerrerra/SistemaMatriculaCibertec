# Informe base del proyecto

## Justificacion e importancia

El sistema reduce el registro manual y disperso de alumnos, cursos y matriculas en una entidad educativa. Centraliza la informacion y valida reglas que evitan DNI duplicados, matriculas repetidas y eliminacion de registros vinculados. Los beneficiarios directos son el personal de registro y los alumnos; los indirectos son docentes y coordinadores, porque reciben informacion mas ordenada para la gestion academica.

## Objetivos SMART

1. Implementar antes de la sustentacion una aplicacion Java con interfaz grafica que permita registrar, actualizar, consultar y almacenar alumnos y cursos sin codigos ni DNI duplicados.
2. Automatizar el registro de matriculas y retiros generando correlativos, fecha y hora del sistema, y producir tres reportes para apoyar el control academico.

## Conclusiones

1. Separar las entidades, las reglas y la interfaz facilita mantener y probar el sistema.
2. Las validaciones de estado impiden operaciones incoherentes durante matricula y retiro.
3. La persistencia en archivos permite conservar la informacion sin requerir una base de datos externa.

## Recomendaciones

1. Validar el programa con casos de alumnos registrados, matriculados y retirados antes de la demostracion.
2. Respaldar la carpeta `data` antes de realizar pruebas masivas.
3. Como mejora futura, reemplazar los archivos por una base de datos y agregar autenticacion de usuarios.
