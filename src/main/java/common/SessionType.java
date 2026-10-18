/*
 * ====================================================================
 *  PART    : COMMON (shared by M1-M4)
 *  PURPOSE : Fixed set of values for SessionType (stored as text in MySQL).
 *  PLUGS IN: Used by models/DAOs of every member.
 *  OOP     : Enum (type safety instead of loose Strings).
 * ====================================================================
 */
package common;

public enum SessionType {
    THEORY, PRACTICAL
}
