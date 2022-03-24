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
/**
 * 
 */
package com.edb.gridsql.parser.handler;

import com.edb.gridsql.parser.core.syntaxtree.NodeToken;
import com.edb.gridsql.parser.core.visitor.ObjectDepthFirst;

/**
 * Traverse syntax tree and returns image of tokens it is built of as a String.
 * Image is not exact: there is exactly one witespace between tokens, it does
 * not matter what was in the original string.
 * 
 * 
 */
public class RawImageHandler extends ObjectDepthFirst {
    private String image;

    /**
     * 
     */
    public RawImageHandler() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.edb.gridsql.Parser.ParserCore.visitor.ObjectDepthFirst#visit(com.edb.gridsql.Parser.ParserCore.syntaxtree.NodeToken,
     *      java.lang.Object)
     */
    @Override
    public Object visit(NodeToken n, Object argu) {
        if (image == null) {
            image = n.tokenImage;
        } else {
            image += " " + n.tokenImage;
        }
        return null;
    }

    /**
     * 
     * @return 
     */
    public String getImage() {
        return image;
    }
}
