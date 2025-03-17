CREATE OR REPLACE FUNCTION calculate_area(length DECIMAL(10, 2), width DECIMAL(10, 2))
RETURNS DECIMAL(10, 2)
LANGUAGE JAVASCRIPT
AS
$$
    if (le
    ngth <= 0 || width <= 0) {
        return null; // Invalid dimensions
    }
    return length * width;
$$;

related pull requests , commits 