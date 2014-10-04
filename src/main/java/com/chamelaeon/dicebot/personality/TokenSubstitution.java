package com.chamelaeon.dicebot.personality;

/**
 * @author Chamelaeon
 */
public class TokenSubstitution {

    private final String token;
    private final String substitution;
    
    /**
     * 
     * @param token
     * @param substitution
     */
    public TokenSubstitution(String token, Object substitution) {
        this(token, substitution.toString());
    }
    
    /**
     * 
     * @param token
     * @param substitution
     */
    public TokenSubstitution(String token, String substitution) {
        this.token = token;
        this.substitution = substitution;
    }

    public String getToken() {
        return token;
    }

    public String getSubstitution() {
        return substitution;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((substitution == null) ? 0 : substitution.hashCode());
        result = prime * result + ((token == null) ? 0 : token.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        TokenSubstitution other = (TokenSubstitution) obj;
        if (substitution == null) {
            if (other.substitution != null) {
                return false;
            }
        } else if (!substitution.equals(other.substitution)) {
            return false;
        }
        if (token == null) {
            if (other.token != null) {
                return false;
            }
        } else if (!token.equals(other.token)) {
            return false;
        }
        return true;
    }
    
    
}
