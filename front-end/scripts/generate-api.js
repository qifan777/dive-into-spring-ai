/* eslint-env node */
import http from 'http'
import fs from 'fs'
import fse from 'fs-extra'
import { v4 as uuidv4 } from 'uuid'
import os from 'os'
import path from 'path'
import AdmZip from 'adm-zip'
const sourceUrl = 'http://localhost:9902/ts.zip'
const tmpFilePath = os.tmpdir() + '/' + uuidv4() + '.zip'
const generatePath = 'src/apis/__generated'

console.log('Downloading ' + sourceUrl + '...')

const tmpFile = fs.createWriteStream(tmpFilePath)
http.get(sourceUrl, (response) => {
  response.pipe(tmpFile)
  tmpFile.on('finish', () => {
    tmpFile.close()
    console.log('File save success: ', tmpFilePath)

    // Remove generatePath if it exists
    if (fs.existsSync(generatePath)) {
      console.log('Removing existing generatePath...')
      fse.removeSync(generatePath)
      console.log('Existing generatePath removed.')
    }

    // Unzip the file using adm-zip
    console.log('Unzipping the file...')
    const zip = new AdmZip(tmpFilePath)
    zip.extractAllTo(generatePath, true)
    console.log('File unzipped successfully.')
    // Remove the temporary file
    console.log('Removing temporary file...')
    fs.unlink(tmpFilePath, (err) => {
      if (err) {
        console.error('Error while removing temporary file:', err)
      } else {
        console.log('Temporary file removed.')
      }
    })
    traverseDirectory(modelPath)
    traverseDirectory(servicePath)
  })
})

// 替换目录路径
const modelPath = 'src/apis/__generated/model'
const servicePath = 'src/apis/__generated/services'

// 递归遍历目录中的所有文件
function traverseDirectory(directoryPath) {
  const files = fs.readdirSync(directoryPath)

  files.forEach((file) => {
    const filePath = path.join(directoryPath, file)
    const stats = fs.statSync(filePath)

    if (stats.isDirectory()) {
      traverseDirectory(filePath)
    } else if (stats.isFile() && path.extname(filePath) === '.ts') {
      replaceInFile(filePath)
    }
  })
}

// 替换文件中的文本
function replaceInFile(filePath) {
  const fileContent = fs.readFileSync(filePath, 'utf8')
  const updatedContent = fileContent
    .replaceAll('readonly ', '')
    .replace(/ReadonlyArray/g, 'Array')
    .replaceAll('ReadonlyMap', 'Map')
    .replace(/Map<(\S+), (\S+)>/g, '{ [key: $1]: $2 }')
  // .replace(/query: (\S+)/g, 'query: T')
  fs.writeFileSync(filePath, updatedContent, 'utf8')
}
