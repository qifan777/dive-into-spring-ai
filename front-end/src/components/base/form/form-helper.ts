import { type Ref, ref } from 'vue'
export const useFormHelper = <T extends { id?: string }>(formValue: T) => {
  const formData = ref({ ...formValue }) as Ref<T>
  const restForm = (form?: T) => {
    formData.value = { ...formValue, id: form?.id }
  }
  return { formData, restForm }
}
