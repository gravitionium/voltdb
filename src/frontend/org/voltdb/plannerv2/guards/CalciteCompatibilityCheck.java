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

/**
 * Check if a SQL statement should be routed to Calcite planner.
 * This is a temporary check before we can make Calcite support all the VoltDB syntaxes.
 *
 * @author Yiqun Zhang
 * @since 9.0
 */
public abstract class CalciteCompatibilityCheck {

    /**
     * The next check in the chain.
     */
    CalciteCompatibilityCheck m_next;

    /**
     * All the {@link CalciteCompatibilityCheck} subclasses should implement this method
     * to do their own checks and return a result.
     *
     * @param sql the SQL statement to check
     * @return true if the SQL statement passed the check.
     */
    protected abstract boolean doCheck(String sql);

    /**
     * @return true if the result of the current check should become final and
     *         no subsequent checks should be visited.
     */
    protected boolean isFinal() {
        return false;
    }

    /**
     * Start the chained check to see if the SQL statement should be routed to Calcite.
     * A query is permissible when at least one positive check in the chain passed (returns true);
     *
     * @param sql the SQL statement to check.
     * @return true if this statement should be routed to Calcite.
     */
    public final boolean check(String sql) {
        if (doCheck(sql)) {
            return true;
        } else if (m_next == null || isFinal()) {
            return false;
        } else {
            return m_next.check(sql);
        }
    }

    /**
     * Add another {@link CalciteCompatibilityCheck} instance to the end of the chain
     * and return the newly appended {@code CalciteCompatibilityCheck} instance.
     *
     * @param next the instance to append.
     * @return the instance appended.
     */
    public final CalciteCompatibilityCheck addNext(CalciteCompatibilityCheck next) {
        m_next = next;
        return m_next;
    }

    /**
     * The factory method to create a check chain.
     *
     * @return The head of the chain.
     */
    public static CalciteCompatibilityCheck chain(CalciteCompatibilityCheck...checks) {
        if (checks == null || checks.length == 0) {
            return null;
        }
        CalciteCompatibilityCheck head, cur;
        cur = head = checks[0];
        for (int i = 1; i < checks.length; ++i) {
            cur = cur.addNext(checks[i]);
        }
        return head;
    }
}