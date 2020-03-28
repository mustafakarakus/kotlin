/*
 * Copyright 2010-2016 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.kotlin.ir.declarations.impl

import org.jetbrains.kotlin.descriptors.ClassConstructorDescriptor
import org.jetbrains.kotlin.descriptors.Visibility
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.declarations.impl.carriers.ConstructorCarrier
import org.jetbrains.kotlin.ir.expressions.IrBody
import org.jetbrains.kotlin.ir.symbols.IrConstructorSymbol
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.visitors.IrElementVisitor
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.resolve.descriptorUtil.isEffectivelyExternal

class IrConstructorImpl(
    startOffset: Int,
    endOffset: Int,
    origin: IrDeclarationOrigin,
    override val symbol: IrConstructorSymbol,
    name: Name = symbol.trueDescriptor.name,
    visibility: Visibility = symbol.trueDescriptor.visibility,
    returnType: IrType,
    isInline: Boolean = symbol.trueDescriptor.isInline,
    isExternal: Boolean = symbol.trueDescriptor.isEffectivelyExternal(),
    override val isPrimary: Boolean = symbol.trueDescriptor.isPrimary,
    isExpect: Boolean = symbol.trueDescriptor.isExpect
) :
    IrFunctionBase<ConstructorCarrier>(
        startOffset, endOffset, origin, name,
        visibility,
        isInline, isExternal, isExpect,
        returnType
    ),
    IrConstructor,
    ConstructorCarrier {

    constructor(
        startOffset: Int,
        endOffset: Int,
        origin: IrDeclarationOrigin,
        symbol: IrConstructorSymbol,
        returnType: IrType,
        body: IrBody? = null
    ) : this(
        startOffset, endOffset, origin, symbol,
        symbol.trueDescriptor.name,
        symbol.trueDescriptor.visibility,
        returnType,
        isInline = symbol.trueDescriptor.isInline,
        isExternal = symbol.trueDescriptor.isEffectivelyExternal(),
        isPrimary = symbol.trueDescriptor.isPrimary,
        isExpect = symbol.trueDescriptor.isExpect
    ) {
        this.body = body
    }

    init {
        symbol.bind(this)
    }

    override val descriptor: ClassConstructorDescriptor get() = symbol.descriptor

    override fun <R, D> accept(visitor: IrElementVisitor<R, D>, data: D): R =
        visitor.visitConstructor(this, data)
}
