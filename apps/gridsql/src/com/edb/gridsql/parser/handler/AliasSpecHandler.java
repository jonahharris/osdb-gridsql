/*****************************************************************************
 * Copyright (C) 2008 EnterpriseDB Corporation.
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 as published by
 * the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see http://www.gnu.org/licenses or write to the
 * Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 *
 * You can contact EnterpriseDB, Inc. via its website at
 * http://www.enterprisedb.com
 *
 ****************************************************************************/
package com.edb.gridsql.parser.handler;

import com.edb.gridsql.exception.ErrorMessageRepository;
import com.edb.gridsql.exception.XDBServerException;
import com.edb.gridsql.parser.core.syntaxtree.AliasName;
import com.edb.gridsql.parser.core.visitor.ObjectDepthFirst;

/**
 * This Alias Spec handler is used for holding information about alias name.
  */
public class AliasSpecHandler extends ObjectDepthFirst {

    private String aliasName;

    /**
     * It returns the alias name.
     *
     * @return A string which is the alias name
     * @throws XDBServerException
     *             If the alias name is null we throw an alias not found
     *             exception though this thing should not occur as the parser
     *             will throw an exception if the signature does not match a
     *             Alias Specification
     */

    public String getAliasName() {
        if (aliasName == null) {
            throw new XDBServerException(
                    ErrorMessageRepository.ALIAS_NAME_NOT_SET, 0,
                    ErrorMessageRepository.ALIAS_NAME_NOT_SET_CODE);
        }
        return aliasName;
    }

    /**
     * Grammar production:
     * f0 -> Identifier(prn)
     * @param n
     * @param argu
     * @return
     */
    @Override
    public Object visit(AliasName n, Object argu) {
        aliasName = (String) n.f0.accept(new IdentifierHandler(), argu);
        return null;
    }
}
