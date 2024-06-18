import { ElMessage } from 'element-plus'
import type { ValidateFieldsError } from 'async-validator/dist-types'

export const assertFormValidate = (callback: () => void) => {
  return (valid: boolean, fields: ValidateFieldsError | undefined) => {
    if (valid) {
      callback()
    } else {
      if (fields) {
        const messages: string[] = []
        for (const field in fields) {
          fields[field].forEach((error) => {
            if (error.message) {
              messages.push(error.message)
            }
          })
        }
        ElMessage.error({ message: messages.join('\n') })
      }
    }
  }
}
