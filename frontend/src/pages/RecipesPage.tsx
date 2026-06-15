import { FormEvent, useMemo, useState } from "react";
import { apiClient, ApiError } from "../api/client";
import { difficultyLabels, difficultyLevels } from "../constants";
import { Button } from "../components/Button";
import { Icon } from "../components/Icon";
import { InputField, SelectField, TextAreaField } from "../components/Field";
import { Notice } from "../components/Notice";
import { Panel } from "../components/Panel";
import { DifficultyBadge } from "../components/StatusBadge";
import { useAsyncResource } from "../hooks/useAsyncResource";
import type { Ingredient, Recipe, RecipePayload } from "../types";
import { flattenApiErrors, getErrorMessage } from "../utils/format";

interface RecipesPageProps {
  refreshKey: number;
}

type RecipeFormState = RecipePayload;

const emptyIngredient: Ingredient = {
  name: "",
  notes: "",
  optional: false,
  quantity: 1,
  unit: "litros"
};

const emptyRecipe: RecipeFormState = {
  name: "",
  description: "",
  ingredients: [{ ...emptyIngredient }],
  defaultMilkVolume: 2,
  defaultStarterAmount: 2,
  heatingTemperature: 85,
  heatingDuration: 30,
  inoculationTemperature: 43,
  incubationTemperature: 43,
  minIncubationTime: 6,
  maxIncubationTime: 10,
  refrigerationTime: 8,
  difficulty: "BEGINNER",
  tips: ""
};

export function RecipesPage({ refreshKey }: RecipesPageProps) {
  const [query, setQuery] = useState("");
  const [editingRecipe, setEditingRecipe] = useState<Recipe | null>(null);
  const [form, setForm] = useState<RecipeFormState>(emptyRecipe);
  const [saving, setSaving] = useState(false);
  const [message, setMessage] = useState<string | null>(null);
  const [formError, setFormError] = useState<string | null>(null);

  const { data, error, loading, reload } = useAsyncResource<Recipe[]>(
    () => (query.trim() ? apiClient.searchRecipes(query.trim()) : apiClient.getRecipes()),
    [query, refreshKey]
  );

  const recipes = data ?? [];
  const selectedRecipeId = editingRecipe?.id ?? null;

  const ingredientCount = useMemo(
    () => form.ingredients.filter((ingredient) => ingredient.name.trim()).length,
    [form.ingredients]
  );

  function startCreate() {
    setEditingRecipe(null);
    setForm(cloneRecipeForm(emptyRecipe));
    setFormError(null);
    setMessage(null);
  }

  function startEdit(recipe: Recipe) {
    setEditingRecipe(recipe);
    setForm(recipeToForm(recipe));
    setFormError(null);
    setMessage(null);
  }

  async function submitRecipe(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setSaving(true);
    setFormError(null);
    setMessage(null);

    try {
      const payload = normalizePayload(form);
      if (editingRecipe) {
        await apiClient.updateRecipe(editingRecipe.id, payload);
        setMessage("Receta actualizada correctamente.");
      } else {
        await apiClient.createRecipe(payload);
        setMessage("Receta creada correctamente.");
      }
      startCreate();
      await reload();
    } catch (error) {
      setFormError(buildErrorMessage(error));
    } finally {
      setSaving(false);
    }
  }

  async function toggleRecipe(recipe: Recipe) {
    setMessage(null);
    setFormError(null);
    try {
      if (recipe.active) {
        await apiClient.deactivateRecipe(recipe.id);
        setMessage("Receta desactivada. Ya no aparecera para iniciar nuevos lotes.");
      } else {
        await apiClient.activateRecipe(recipe.id);
        setMessage("Receta activada.");
      }
      await reload();
    } catch (error) {
      setFormError(buildErrorMessage(error));
    }
  }

  function updateField<K extends keyof RecipeFormState>(field: K, value: RecipeFormState[K]) {
    setForm((current) => ({ ...current, [field]: value }));
  }

  function updateIngredient(index: number, value: Ingredient) {
    setForm((current) => ({
      ...current,
      ingredients: current.ingredients.map((ingredient, itemIndex) => (itemIndex === index ? value : ingredient))
    }));
  }

  function addIngredient() {
    setForm((current) => ({
      ...current,
      ingredients: [...current.ingredients, { ...emptyIngredient }]
    }));
  }

  function removeIngredient(index: number) {
    setForm((current) => ({
      ...current,
      ingredients:
        current.ingredients.length === 1
          ? [{ ...emptyIngredient }]
          : current.ingredients.filter((_, itemIndex) => itemIndex !== index)
    }));
  }

  return (
    <div className="content-grid">
      {message ? <Notice message={message} tone="success" /> : null}
      {formError ? <Notice message={formError} title="No se pudo guardar" tone="error" /> : null}

      <div className="split-layout">
        <Panel
          actions={
            <Button icon={<Icon name="plus" />} onClick={startCreate} variant="primary">
              Nueva receta
            </Button>
          }
          eyebrow="Catalogo"
          title="Recetas activas y buscador"
        >
          <div className="toolbar">
            <InputField
              label="Buscar"
              onChange={(event) => setQuery(event.target.value)}
              placeholder="Nombre o descripcion"
              value={query}
            />
          </div>

          {loading ? <Notice message="Cargando recetas..." /> : null}
          {error ? <Notice message={getErrorMessage(error)} title="Error al cargar recetas" tone="error" /> : null}

          {!loading && !error && recipes.length === 0 ? <Notice message="No hay recetas para este filtro." /> : null}

          <div className="card-list">
            {recipes.map((recipe) => (
              <article className={`list-card ${selectedRecipeId === recipe.id ? "is-selected" : ""}`} key={recipe.id}>
                <div>
                  <div className="list-card-title">
                    <h3>{recipe.name}</h3>
                    <DifficultyBadge difficulty={recipe.difficulty} />
                  </div>
                  <p>{recipe.description || "Sin descripcion registrada."}</p>
                  <dl className="compact-facts">
                    <div>
                      <dt>Leche</dt>
                      <dd>{recipe.defaultMilkVolume} L</dd>
                    </div>
                    <div>
                      <dt>Incubacion</dt>
                      <dd>
                        {recipe.minIncubationTime}-{recipe.maxIncubationTime} h
                      </dd>
                    </div>
                    <div>
                      <dt>Ingredientes</dt>
                      <dd>{recipe.ingredients?.length ?? 0}</dd>
                    </div>
                  </dl>
                </div>
                <div className="card-actions">
                  <Button icon={<Icon name="edit" />} onClick={() => startEdit(recipe)}>
                    Editar
                  </Button>
                  <Button onClick={() => void toggleRecipe(recipe)} variant={recipe.active ? "danger" : "secondary"}>
                    {recipe.active ? "Desactivar" : "Activar"}
                  </Button>
                </div>
              </article>
            ))}
          </div>
        </Panel>

        <Panel
          eyebrow={editingRecipe ? `Editando #${editingRecipe.id}` : "Nueva receta"}
          title={editingRecipe ? editingRecipe.name : "Registrar receta"}
        >
          <form className="recipe-form" onSubmit={(event) => void submitRecipe(event)}>
            <div className="form-grid">
              <InputField
                label="Nombre"
                minLength={3}
                onChange={(event) => updateField("name", event.target.value)}
                required
                value={form.name}
              />
              <SelectField
                label="Dificultad"
                onChange={(event) => updateField("difficulty", event.target.value as RecipeFormState["difficulty"])}
                value={form.difficulty}
              >
                {difficultyLevels.map((level) => (
                  <option key={level} value={level}>
                    {difficultyLabels[level]}
                  </option>
                ))}
              </SelectField>
            </div>

            <TextAreaField
              label="Descripcion"
              maxLength={500}
              onChange={(event) => updateField("description", event.target.value)}
              value={form.description}
            />

            <div className="form-grid three">
              <InputField
                label="Leche por defecto"
                min="0.1"
                onChange={(event) => updateField("defaultMilkVolume", toNumber(event.target.value))}
                required
                step="0.1"
                type="number"
                value={form.defaultMilkVolume}
              />
              <InputField
                label="Fermento por defecto"
                min="0.5"
                onChange={(event) => updateField("defaultStarterAmount", toNumber(event.target.value))}
                required
                step="0.1"
                type="number"
                value={form.defaultStarterAmount}
              />
              <InputField
                label="Refrigeracion"
                min="2"
                onChange={(event) => updateField("refrigerationTime", toNumber(event.target.value))}
                required
                type="number"
                value={form.refrigerationTime}
              />
            </div>

            <div className="form-grid three">
              <InputField
                label="Temperatura de calentamiento"
                max="100"
                min="30"
                onChange={(event) => updateField("heatingTemperature", toNumber(event.target.value))}
                required
                step="0.1"
                type="number"
                value={form.heatingTemperature}
              />
              <InputField
                label="Duracion calentamiento"
                min="5"
                onChange={(event) => updateField("heatingDuration", toNumber(event.target.value))}
                required
                type="number"
                value={form.heatingDuration}
              />
              <InputField
                label="Temperatura inoculacion"
                max="50"
                min="30"
                onChange={(event) => updateField("inoculationTemperature", toNumber(event.target.value))}
                required
                step="0.1"
                type="number"
                value={form.inoculationTemperature}
              />
            </div>

            <div className="form-grid three">
              <InputField
                label="Temperatura incubacion"
                max="50"
                min="35"
                onChange={(event) => updateField("incubationTemperature", toNumber(event.target.value))}
                required
                step="0.1"
                type="number"
                value={form.incubationTemperature}
              />
              <InputField
                label="Incubacion minima"
                min="4"
                onChange={(event) => updateField("minIncubationTime", toNumber(event.target.value))}
                required
                type="number"
                value={form.minIncubationTime}
              />
              <InputField
                label="Incubacion maxima"
                max="24"
                min="4"
                onChange={(event) => updateField("maxIncubationTime", toNumber(event.target.value))}
                required
                type="number"
                value={form.maxIncubationTime}
              />
            </div>

            <TextAreaField
              label="Consejos"
              maxLength={500}
              onChange={(event) => updateField("tips", event.target.value)}
              value={form.tips}
            />

            <div className="form-section-header">
              <div>
                <h3>Ingredientes</h3>
                <p>{ingredientCount} ingredientes listos para guardar</p>
              </div>
              <Button icon={<Icon name="plus" />} onClick={addIngredient}>
                Agregar
              </Button>
            </div>

            <div className="ingredient-list">
              {form.ingredients.map((ingredient, index) => (
                <div className="ingredient-row" key={index}>
                  <InputField
                    label="Nombre"
                    onChange={(event) => updateIngredient(index, { ...ingredient, name: event.target.value })}
                    required
                    value={ingredient.name}
                  />
                  <InputField
                    label="Cantidad"
                    min="0.01"
                    onChange={(event) => updateIngredient(index, { ...ingredient, quantity: toNumber(event.target.value) })}
                    required
                    step="0.01"
                    type="number"
                    value={ingredient.quantity}
                  />
                  <InputField
                    label="Unidad"
                    onChange={(event) => updateIngredient(index, { ...ingredient, unit: event.target.value })}
                    required
                    value={ingredient.unit}
                  />
                  <InputField
                    label="Notas"
                    onChange={(event) => updateIngredient(index, { ...ingredient, notes: event.target.value })}
                    value={ingredient.notes ?? ""}
                  />
                  <label className="checkbox-field">
                    <input
                      checked={Boolean(ingredient.optional)}
                      onChange={(event) => updateIngredient(index, { ...ingredient, optional: event.target.checked })}
                      type="checkbox"
                    />
                    <span>Opcional</span>
                  </label>
                  <Button icon={<Icon name="close" />} onClick={() => removeIngredient(index)} variant="ghost">
                    Quitar
                  </Button>
                </div>
              ))}
            </div>

            <div className="form-actions">
              <Button onClick={startCreate} variant="secondary">
                Limpiar
              </Button>
              <Button disabled={saving} icon={<Icon name="check" />} type="submit" variant="primary">
                {saving ? "Guardando..." : editingRecipe ? "Actualizar receta" : "Crear receta"}
              </Button>
            </div>
          </form>
        </Panel>
      </div>
    </div>
  );
}

function recipeToForm(recipe: Recipe): RecipeFormState {
  return {
    name: recipe.name,
    description: recipe.description ?? "",
    ingredients: recipe.ingredients?.length ? recipe.ingredients.map((ingredient) => ({ ...ingredient })) : [{ ...emptyIngredient }],
    defaultMilkVolume: recipe.defaultMilkVolume,
    defaultStarterAmount: recipe.defaultStarterAmount,
    heatingTemperature: recipe.heatingTemperature,
    heatingDuration: recipe.heatingDuration,
    inoculationTemperature: recipe.inoculationTemperature,
    incubationTemperature: recipe.incubationTemperature,
    minIncubationTime: recipe.minIncubationTime,
    maxIncubationTime: recipe.maxIncubationTime,
    refrigerationTime: recipe.refrigerationTime,
    difficulty: recipe.difficulty,
    tips: recipe.tips ?? ""
  };
}

function cloneRecipeForm(recipe: RecipeFormState): RecipeFormState {
  return {
    ...recipe,
    ingredients: recipe.ingredients.map((ingredient) => ({ ...ingredient }))
  };
}

function normalizePayload(form: RecipeFormState): RecipePayload {
  return {
    ...form,
    name: form.name.trim(),
    description: form.description?.trim(),
    tips: form.tips?.trim(),
    ingredients: form.ingredients
      .filter((ingredient) => ingredient.name.trim())
      .map((ingredient) => ({
        name: ingredient.name.trim(),
        notes: ingredient.notes?.trim(),
        optional: Boolean(ingredient.optional),
        quantity: ingredient.quantity,
        unit: ingredient.unit.trim()
      }))
  };
}

function toNumber(value: string) {
  return Number(value);
}

function buildErrorMessage(error: unknown) {
  if (error instanceof ApiError) {
    const details = flattenApiErrors(error.details);
    return [error.message, ...details].filter(Boolean).join(" | ");
  }
  return getErrorMessage(error);
}
