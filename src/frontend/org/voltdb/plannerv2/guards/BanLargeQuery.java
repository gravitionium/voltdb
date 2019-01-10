/* This file is part of VoltDB.
 * Copyright (C) 2008-2019 VoltDB Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with VoltDB.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.voltdb.plannerv2.guards;

import org.voltdb.VoltDB;

/**
 * Large queries cannot be handled by Calcite now.
 * Fail the check if the large query mode is enabled.
 *
 * @author Yiqun Zhang
 * @since 9.0
 */
public class BanLargeQuery extends CalciteCompatibilityCheck {

    private static final boolean s_isLargeTempTableTarget =
            VoltDB.instance().getBackendTargetType().isLargeTempTableTarget;

    @Override protected final boolean doCheck(String sql) {
        return ! s_isLargeTempTableTarget;
    }

    @Override protected boolean isFinal() {
        return s_isLargeTempTableTarget;
    }
}