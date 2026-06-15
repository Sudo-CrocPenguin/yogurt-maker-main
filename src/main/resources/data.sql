-- Archivo de inicialización de datos
-- Se carga solo cuando un perfil habilita spring.sql.init.mode=always.
-- El perfil dev lo usa para que el frontend tenga recetas listas al arrancar.

INSERT INTO recipes (
    name,
    description,
    default_milk_volume,
    default_starter_amount,
    heating_temperature,
    heating_duration,
    inoculation_temperature,
    incubation_temperature,
    min_incubation_time,
    max_incubation_time,
    refrigeration_time,
    difficulty,
    tips,
    active
) VALUES
(
    'Yogurt Natural Clasico',
    'Receta base para yogurt natural de textura cremosa y acidez suave.',
    2.0,
    2.0,
    85.0,
    30,
    43.0,
    43.0,
    6,
    10,
    8,
    'BEGINNER',
    'Use leche entera para mayor cremosidad y evite mover el lote durante la incubacion.',
    true
),
(
    'Yogurt Griego Espeso',
    'Version con mayor cuerpo, pensada para colar despues de la fermentacion.',
    3.0,
    3.0,
    85.0,
    35,
    42.0,
    42.0,
    8,
    12,
    10,
    'INTERMEDIATE',
    'Despues de refrigerar, cuele el yogurt entre 3 y 4 horas para concentrar la textura.',
    true
),
(
    'Yogurt Sin Lactosa',
    'Receta adaptada para leche sin lactosa con control cuidadoso de temperatura.',
    2.0,
    2.0,
    82.0,
    25,
    42.0,
    42.0,
    6,
    9,
    8,
    'BEGINNER',
    'Use leche sin lactosa ultrapasteurizada y mantenga utensilios bien sanitizados.',
    true
);

INSERT INTO ingredients (name, quantity, unit, notes, optional, recipe_id)
SELECT 'Leche entera', 2.0, 'litros', 'Preferiblemente fresca o pasteurizada', false, id
FROM recipes
WHERE name = 'Yogurt Natural Clasico';

INSERT INTO ingredients (name, quantity, unit, notes, optional, recipe_id)
SELECT 'Yogurt natural con cultivos vivos', 2.0, 'cucharadas', 'Funciona como cultivo iniciador', false, id
FROM recipes
WHERE name = 'Yogurt Natural Clasico';

INSERT INTO ingredients (name, quantity, unit, notes, optional, recipe_id)
SELECT 'Leche entera', 3.0, 'litros', 'Mayor volumen para compensar el colado', false, id
FROM recipes
WHERE name = 'Yogurt Griego Espeso';

INSERT INTO ingredients (name, quantity, unit, notes, optional, recipe_id)
SELECT 'Cultivo iniciador', 3.0, 'cucharadas', 'Debe contener cultivos activos', false, id
FROM recipes
WHERE name = 'Yogurt Griego Espeso';

INSERT INTO ingredients (name, quantity, unit, notes, optional, recipe_id)
SELECT 'Filtro o tela para colar', 1.0, 'unidad', 'Se usa despues de completar el proceso base', true, id
FROM recipes
WHERE name = 'Yogurt Griego Espeso';

INSERT INTO ingredients (name, quantity, unit, notes, optional, recipe_id)
SELECT 'Leche sin lactosa', 2.0, 'litros', 'Usar producto sin lactosa y sin saborizantes', false, id
FROM recipes
WHERE name = 'Yogurt Sin Lactosa';

INSERT INTO ingredients (name, quantity, unit, notes, optional, recipe_id)
SELECT 'Cultivo iniciador', 2.0, 'cucharadas', 'Verificar que no contenga azucares agregados', false, id
FROM recipes
WHERE name = 'Yogurt Sin Lactosa';
