package com.adform.sdk.utils;

/**
 * Created by mariusm on 27/05/14.
 */
public class AdformEnum {
    /**
     * Helps to describe ad visibility depending on contract load state.
     * @see com.adform.sdk.view.base.BaseCoreContainer
     */
    public enum VisibilityGeneralState {
        LOAD_SUCCESSFUL(0),
        LOAD_FAIL(1);

        private int value;

        private VisibilityGeneralState(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static VisibilityGeneralState parseType(int status) {
            switch (status) {
                case 0: return LOAD_SUCCESSFUL;
                case 1: return LOAD_FAIL;
                default: return LOAD_SUCCESSFUL;
            }
        }
        public static String printType(VisibilityGeneralState state) {
            switch (state) {
                case LOAD_SUCCESSFUL: return "LOAD_SUCCESSFUL";
                case LOAD_FAIL: return "LOAD_FAIL";
            }
            return null;
        }

    }

    /**
     * Helps to describe ad visibility depending on ad coordinates in the window.
     * @see com.adform.sdk.utils.managers.VisibilityPositionManager
     * @see com.adform.sdk.view.base.BaseCoreContainer
     */
    public enum VisibilityOnScreenState {
        ON_SCREEN(0),
        OFF_SCREEN(1);
        private int value;

        private VisibilityOnScreenState(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static VisibilityOnScreenState parseType(int status) {
            switch (status) {
                case 1:
                    return ON_SCREEN;
                case 2:
                    return OFF_SCREEN;
                default:
                    return OFF_SCREEN;
            }
        }

        public static String printType(VisibilityOnScreenState state) {
            switch (state) {
                case ON_SCREEN:
                    return "ON_SCREEN";
                case OFF_SCREEN:
                    return "OFF_SCREEN";
            }
            return null;
        }
    }

    /**
     * Describes available states for the mraid
     */
    public enum State {
        LOADING(0),
        DEFAULT(1),
        EXPANDED(2),
        RESIZED(3),
        HIDDEN(4);
        private int value;

        private State(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static State parseType(int status) {
            switch (status) {
                case 0: return LOADING;
                case 1: return DEFAULT;
                case 2: return EXPANDED;
                case 3: return RESIZED;
                case 4: return HIDDEN;
                default: return LOADING;
            }
        }

        public static String getStateString(State state) {
            switch (state) {
                case LOADING: return "loading";
                case DEFAULT: return "default";
                case EXPANDED: return "expanded";
                case RESIZED: return "resized";
                case HIDDEN: return "hidden";
            }
            return null;
        }

    }

    /**
     * Describes mraid ad placement type
     */
    public enum PlacementType {
        UNKNOWN(-1),
        INLINE(0),
        INTERSTITIAL(1);
        private int value;

        private PlacementType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static PlacementType parseType(int status) {
            switch (status) {
                case 0: return INLINE;
                case 1: return INTERSTITIAL;
                default: return UNKNOWN;
            }
        }

        public static String getPlacementString(PlacementType placementType) {
            switch (placementType) {
                case INLINE: return "inline";
                case INTERSTITIAL: return "interstitial";
                case UNKNOWN: return "unknown";
            }
            return null;
        }

    }
    /**
     * Describes orientation that is being forced to display
     */
    public enum ForcedOrientation {
        UNKNOWN(-1),
        NONE(0),
        LANDSCAPE(1),
        PORTRAIT(2);
        private int value;

        private ForcedOrientation(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static ForcedOrientation parseType(int type) {
            switch (type) {
                case 0: return NONE;
                case 1: return LANDSCAPE;
                case 2: return PORTRAIT;
                default: return UNKNOWN;
            }
        }
        public static ForcedOrientation parseType(String type) {
            if (type == null)
                return UNKNOWN;
            if (type.equals("portrait"))
                return PORTRAIT;
            else if (type.equals("landscape"))
                return LANDSCAPE;
            else if (type.equals("none"))
                return NONE;
            else
                return UNKNOWN;
        }
    }

}
