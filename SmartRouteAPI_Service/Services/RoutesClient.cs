using System;
using System.Linq;
using System.Net.Http;
using System.Net.Http.Json;
using System.Threading.Tasks;
using Microsoft.Extensions.Configuration;
using TripsApi.Services.Google;

namespace TripsApi.Services
{
    public class RoutesClient : IRoutesClient
    {
        private readonly HttpClient httpClient;
        private readonly string apiKey;
        public RoutesClient(HttpClient httpClient, IConfiguration config)
        {
            this.httpClient = httpClient;
            apiKey = config["Google:ApiKey"] ?? "";
        }
        public async Task<ComputeRoutesResponse> OptimizeRouteAsync(string[] stops)
        {
            var origin = stops.First();
            var destination = stops.Last();
            var waypointList = stops.Skip(1).Take(stops.Length - 2);
            var waypoints = string.Join("|", waypointList);
            var url = $"https://maps.googleapis.com/maps/api/directions/json?origin={Uri.EscapeDataString(origin)}&destination={Uri.EscapeDataString(destination)}";
            if (!string.IsNullOrEmpty(waypoints))
                url += $"&waypoints=optimize:true|{Uri.EscapeDataString(waypoints)}";
            url += $"&key={apiKey}";
            var response = await httpClient.GetFromJsonAsync<ComputeRoutesResponse>(url);
            if (response == null || response.Status != "OK")
                throw new InvalidOperationException("Google API error");
            return response;
        }
    }
}
