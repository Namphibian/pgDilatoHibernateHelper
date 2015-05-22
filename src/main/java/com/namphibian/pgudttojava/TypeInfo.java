/**
 * Copyright (c) 2013, impossibl.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *  * Neither the name of impossibl.com nor the names of its contributors may
 *    be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.namphibian.pgudttojava;

import com.impossibl.postgres.types.CompositeType;
import com.impossibl.postgres.types.CompositeType.Attribute;

import java.util.ArrayList;
import java.util.List;



/**
 * Provides information for a composite type
 *
 * @author kdubb
 *
 */
public class TypeInfo {

  CompositeType type;

  public TypeInfo(CompositeType type) {
    this.type = type;
  }

  public CompositeType getSource() {
    return type;
  }

  public String getClassName() {
    return Names.getFullCamelCase(type.getName());
  }

  public List<AttributeInfo> getAttributeInfos() {

    List<AttributeInfo> attrInfos = new ArrayList<>();

    for (Attribute attr : type.getAttributes()) {
      attrInfos.add(new AttributeInfo(attr));
    }

    return attrInfos;
  }
   
  @Override
  public String toString() 
  {
    StringBuilder builder = new StringBuilder();
    builder.append("Type Name: ");
    builder.append(type.getName());
    builder.append(" Camel Case Name: ");
    builder.append(Names.getFullCamelCase(type.getName()));
    for (AttributeInfo attr : getAttributeInfos()) 
    {
       // attrInfos.add(new AttributeInfo(attr));
        builder.append(" Property:");
        builder.append(attr.getPropertyName());
        
    }
    builder.append("\n");
    return builder.toString();
  }

}
