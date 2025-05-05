using System;
using System.Linq;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using TripsApi.Data;
using TripsApi.Models;
using TripsApi.Services;
using TripsApi.Utils;

namespace TripsApi.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class TripsController : ControllerBase
    {
        private readonly TripsContext context;
        private readonly IRoutesClient routesClient;

        public TripsController(TripsContext context, IRoutesClient routesClient)
        {
            this.context = context;
            this.routesClient = routesClient;
        }

        // GET /api/trips
        [HttpGet]
        public IActionResult GetAll()
        {
            var list = context.Trips
                .Include(t => t.Steps.OrderBy(s => s.Order))
                .OrderBy(t => t.Id)
                .ToList();
            return Ok(list);
        }

        // GET /api/trips/code/{code}
        [HttpGet("code/{code}")]
        public IActionResult GetByCode(string code)
        {
            var trip = context.Trips
                .Include(t => t.Steps.OrderBy(s => s.Order))
                .FirstOrDefault(t => t.Code == code);
            return trip != null ? Ok(trip) : NotFound();
        }

        // POST /api/trips
        [HttpPost]
        public IActionResult Create([FromBody] TripRequestDto dto)
        {
            if (!ModelState.IsValid) return BadRequest();

            var key = string.Join("||", dto.Locations);
            var query = string.Join(", ", dto.Locations);
            var name = string.IsNullOrWhiteSpace(dto.Name) ? query : dto.Name;

            var trip = new Trip
            {
                Name = name,
                OriginalQuery = query,
                OriginalQueryKey = key
            };

            var rng = new Random();
            do { trip.Code = rng.Next(0, 1_000_000).ToString("D6"); }
            while (context.Trips.Any(t => t.Code == trip.Code));

            context.Trips.Add(trip);
            context.SaveChanges();

            var existing = context.Trips
                .Include(t => t.Steps)
                .Where(t => t.OriginalQueryKey == key && t.Id != trip.Id)
                .SelectMany(t => t.Steps)
                .OrderBy(s => s.Order)
                .ToList();

            var fromCache = existing.Any();
            if (fromCache)
            {
                foreach (var s in existing)
                {
                    context.TripSteps.Add(new TripStep
                    {
                        TripId = trip.Id,
                        From = s.From,
                        To = s.To,
                        DistanceKm = s.DistanceKm,
                        DurationMinutes = s.DurationMinutes,
                        Order = s.Order,
                        StartLat = s.StartLat,
                        StartLng = s.StartLng,
                        EndLat = s.EndLat,
                        EndLng = s.EndLng,
                        EncodedPolyline = s.EncodedPolyline
                    });
                }
            }
            else
            {
                var response = routesClient
                    .OptimizeRouteAsync(dto.Locations.ToArray())
                    .GetAwaiter().GetResult();

                var route = response.Routes.First();
                trip.OverviewPolyline = route.OverviewPolyline?.Points;

                for (int i = 0; i < route.Legs.Length; i++)
                {
                    var leg = route.Legs[i];
                    var allCoords = leg.Steps
                                       .SelectMany(s => PolylineUtil.Decode(s.Polyline.Points))
                                       .ToList();
                    var legPolyline = PolylineUtil.Encode(allCoords);

                    context.TripSteps.Add(new TripStep
                    {
                        TripId = trip.Id,
                        From = leg.StartAddress,
                        To = leg.EndAddress,
                        DistanceKm = leg.Distance.Value / 1000.0,
                        DurationMinutes = leg.Duration.Value / 60,
                        Order = i,
                        StartLat = leg.StartLocation.Lat,
                        StartLng = leg.StartLocation.Lng,
                        EndLat = leg.EndLocation.Lat,
                        EndLng = leg.EndLocation.Lng,
                        EncodedPolyline = legPolyline
                    });
                }
            }

            context.SaveChanges();

            var result = context.Trips
                .Include(t => t.Steps.OrderBy(s => s.Order))
                .Single(t => t.Id == trip.Id);

            Response.Headers.Append("X-Route-Source", fromCache ? "cache" : "google");
            return CreatedAtAction(nameof(GetByCode), new { code = trip.Code }, result);
        }

        // DELETE /api/trips/code/{code}
        [HttpDelete("code/{code}")]
        public IActionResult Delete(string code)
        {
            var trip = context.Trips
                .Include(t => t.Steps)
                .FirstOrDefault(t => t.Code == code);
            if (trip == null) return NotFound();

            context.TripSteps.RemoveRange(trip.Steps);
            context.Trips.Remove(trip);
            context.SaveChanges();
            return NoContent();
        }

        // PUT /api/trips/code/{code}
        [HttpPut("code/{code}")]
        public IActionResult Update(string code, [FromBody] TripRequestDto dto)
        {
            if (!ModelState.IsValid) return BadRequest();

            var trip = context.Trips
                .Include(t => t.Steps)
                .FirstOrDefault(t => t.Code == code);
            if (trip == null) return NotFound();

            var stops = dto.Locations.ToArray();
            trip.Name = string.IsNullOrWhiteSpace(dto.Name) ? string.Join(", ", stops) : dto.Name;
            trip.OriginalQuery = string.Join(", ", stops);
            trip.OriginalQueryKey = string.Join("||", stops);

            context.TripSteps.RemoveRange(trip.Steps);
            context.SaveChanges();

            var response = routesClient
                .OptimizeRouteAsync(stops)
                .GetAwaiter().GetResult();

            var route = response.Routes.First();
            trip.OverviewPolyline = route.OverviewPolyline?.Points;

            for (int i = 0; i < route.Legs.Length; i++)
            {
                var leg = route.Legs[i];
                var allCoords = leg.Steps
                                   .SelectMany(s => PolylineUtil.Decode(s.Polyline.Points))
                                   .ToList();
                var legPolyline = PolylineUtil.Encode(allCoords);

                context.TripSteps.Add(new TripStep
                {
                    TripId = trip.Id,
                    From = leg.StartAddress,
                    To = leg.EndAddress,
                    DistanceKm = leg.Distance.Value / 1000.0,
                    DurationMinutes = leg.Duration.Value / 60,
                    Order = i,
                    StartLat = leg.StartLocation.Lat,
                    StartLng = leg.StartLocation.Lng,
                    EndLat = leg.EndLocation.Lat,
                    EndLng = leg.EndLocation.Lng,
                    EncodedPolyline = legPolyline
                });
            }

            context.SaveChanges();

            var result = context.Trips
                .Include(t => t.Steps.OrderBy(s => s.Order))
                .Single(t => t.Id == trip.Id);

            return Ok(result);
        }
    }
}