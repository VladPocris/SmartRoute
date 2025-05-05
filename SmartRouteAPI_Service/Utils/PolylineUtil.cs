using System.Text;

namespace TripsApi.Utils
{
    public static class PolylineUtil
    {
        public static List<(double lat, double lng)> Decode(string encoded)
        {
            var poly = new List<(double, double)>();
            int index = 0, len = encoded.Length;
            int lat = 0, lng = 0;

            while (index < len)
            {
                int b, shift = 0, result = 0;
                do
                {
                    b = encoded[index++] - 63;
                    result |= (b & 0x1F) << shift;
                    shift += 5;
                }
                while (b >= 0x20);
                int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lat += dlat;

                shift = 0;
                result = 0;
                do
                {
                    b = encoded[index++] - 63;
                    result |= (b & 0x1F) << shift;
                    shift += 5;
                }
                while (b >= 0x20);
                int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lng += dlng;

                poly.Add((lat / 1E5, lng / 1E5));
            }
            return poly;
        }

        public static string Encode(List<(double lat, double lng)> points)
        {
            var str = new StringBuilder();
            int prevLat = 0, prevLng = 0;

            foreach (var (lat, lng) in points)
            {
                int ilat = (int)Math.Round(lat * 1E5);
                int ilng = (int)Math.Round(lng * 1E5);

                EncodeValue(ilat - prevLat, str);
                EncodeValue(ilng - prevLng, str);

                prevLat = ilat;
                prevLng = ilng;
            }
            return str.ToString();
        }

        private static void EncodeValue(int v, StringBuilder str)
        {
            v <<= 1;
            if (v < 0) v = ~v;
            while (v >= 0x20)
            {
                str.Append((char)((0x20 | (v & 0x1F)) + 63));
                v >>= 5;
            }
            str.Append((char)(v + 63));
        }
    }
}