/*
 * Copyright 2010-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.backend.jvm.codegen

import org.jetbrains.kotlin.codegen.inline.ReifiedTypeInliner
import org.jetbrains.kotlin.codegen.state.GenerationState
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.toKotlinType
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.model.TypeParameterMarker
import org.jetbrains.org.objectweb.asm.commons.InstructionAdapter

class IrInlineIntrinsicsSupport(
    private val state: GenerationState,
    private val typeMapper: IrTypeMapper
) : ReifiedTypeInliner.IntrinsicsSupport<IrType> {
    override fun putClassInstance(v: InstructionAdapter, type: IrType) {
        ExpressionCodegen.generateClassInstance(v, type, typeMapper)
    }

    override fun generateNewTypeParameter(v: InstructionAdapter, typeParameter: TypeParameterMarker) {
        TODO()
    }

    override fun toKotlinType(type: IrType): KotlinType = type.toKotlinType()
}
