/*******************************************************************************
 * Copyright (c) 2007, 2014 Massimiliano Ziccardi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package it.jnrpe.utils.thresholds;

import java.math.BigDecimal;

/**
 * Base class for the number parsing stages.
 *
 * @author Massimiliano Ziccardi
 * @version $Revision: 1.0 $
 */
abstract class NumberBoundaryStage extends Stage {

    /**
     * @param stageName
     *            The name of this stage
     */
    protected NumberBoundaryStage(final String stageName) {
        super(stageName);
    }

    /**
     * Parses the threshold to remove the matched number string.
     *
     * No checks are performed against the passed in string: the object assumes
     * that the string is correct since the {@link #canParse(String)} method
     * <b>must</b> be called <b>before</b> this method.
     *
     * @param threshold
     *            The threshold chunk to be parsed
     * @param tc
     *            The threshold config object. This object will be populated
     *            according to the passed in threshold.
    
    
     * @return the remaining part of the threshold * @throws RangeException
     *             if the threshold can't be parsed */
    @Override
    public String parse(final String threshold, final RangeConfig tc) throws RangeException {
        StringBuilder numberString = new StringBuilder();
        for (int i = 0; i < threshold.length(); i++) {
            if (Character.isDigit(threshold.charAt(i))) {
                numberString.append(threshold.charAt(i));
                continue;
            }
            if (threshold.charAt(i) == '.') {
                if (numberString.toString().endsWith(".")) {
                    numberString.deleteCharAt(numberString.length() - 1);
                    break;
                } else {
                    numberString.append(threshold.charAt(i));
                    continue;
                }
            }
            if (threshold.charAt(i) == '+' || threshold.charAt(i) == '-') {
                if (numberString.length() == 0) {
                    numberString.append(threshold.charAt(i));
                    continue;
                } else {
                    throw new RangeException("Unexpected '" + threshold.charAt(i) + "' sign parsing boundary");
                }
            }
            // throw new InvalidRangeSyntaxException(this,
            // threshold.substring(numberString.length()));
            break;
        }
        if (numberString.length() != 0 && !justSign(numberString.toString())) {
            BigDecimal bd = new BigDecimal(numberString.toString());
            setBoundary(tc, bd);
            return threshold.substring(numberString.length());
        } else {
            throw new InvalidRangeSyntaxException(this, threshold);
        }
    }

    /**
     * @param string
     *            The string to be evaluated
    
     * @return <code>true</code> if the string is just a sign. */
    private boolean justSign(final String string) {
        return "+".equals(string) || "-".equals(string);
    }

    /**
     * Method canParse.
     * @param threshold String
     * @return boolean
     */
    @Override
    public boolean canParse(final String threshold) {
        if (threshold == null || threshold.isEmpty()) {
            return false;
        }
        switch (threshold.charAt(0)) {
        case '+':
        case '-':
            return !(threshold.startsWith("-inf") || threshold.startsWith("+inf"));
        default:
            return Character.isDigit(threshold.charAt(0));
        }
    }

    /**
     * Method expects.
     * @return String
     */
    @Override
    public String expects() {
        return "+-[0-9]";
    }

    /**
     * This object can be used to set both left or right boundary of a range. It
     * is left to the implementing class to set the right boundary inside the
     * {@link RangeConfig} object;
     *
     * @param tc
     *            The threshold configuration
     * @param boundary
     *            The boundary value
     */
    public abstract void setBoundary(final RangeConfig tc, final BigDecimal boundary);

    /**
     * This class represent a left numeric boundary of a range.
     *
     * In the -10..+inf range it represent the '-10' value.
     *
     * @author Massimiliano Ziccardi
     *
     */
    public static class LeftBoundaryStage extends NumberBoundaryStage {

        /**
         * 
         */
        private static final long serialVersionUID = 6686605731843120850L;

        /**
         *
         */
        protected LeftBoundaryStage() {
            super("startboundary");
        }

        /**
         * Method setBoundary.
         * @param tc RangeConfig
         * @param boundary BigDecimal
         */
        @Override
        public void setBoundary(final RangeConfig tc, final BigDecimal boundary) {
            tc.setLeftBoundary(boundary);
        }
    }

    /**
     * This class represent a right numeric boundary of a range.
     *
     * In the range -10..+100.34 it represent the '100.34' value.
     *
     * @author Massimiliano Ziccardi
     *
     */
    public static class RightBoundaryStage extends NumberBoundaryStage {

        /**
         * 
         */
        private static final long serialVersionUID = -4377081825521946333L;

        /**
         *
         */
        protected RightBoundaryStage() {
            super("rightboundary");
        }

        /**
         * Method setBoundary.
         * @param tc RangeConfig
         * @param boundary BigDecimal
         */
        @Override
        public void setBoundary(final RangeConfig tc, final BigDecimal boundary) {
            tc.setRightBoundary(boundary);
        }

        /**
         * Right boundary can be the end of the range.
         *
        
         * @return <code>true</code> */
        public final boolean isLeaf() {
            return true;
        }
    }
}