package io.github.qifan777.knowledge;

import io.qifan.infrastructure.generator.processor.QiFanGenerator;

public class CodeGenerator {
    public static void main(String[] args) {
        QiFanGenerator qiFanGenerator = new QiFanGenerator();
        System.out.println("生成代码");
        qiFanGenerator.process("io.github.qifan777.knowledge", "template");
    }
}
