using Microsoft.EntityFrameworkCore;
using System.Text.Json.Serialization;
using TripsApi.Data;
using TripsApi.Services;

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddDbContext<TripsContext>(opts =>
    opts.UseSqlServer(builder.Configuration.GetConnectionString("TripsContext")));

builder.Services.AddHttpClient<IRoutesClient, RoutesClient>();

builder.Services.AddControllers()
       .AddJsonOptions(opts =>
           opts.JsonSerializerOptions.ReferenceHandler =
               ReferenceHandler.IgnoreCycles);

builder.Services.AddEndpointsApiExplorer();

builder.Services.AddSwaggerGen(c =>
{
    c.EnableAnnotations();
});

var app = builder.Build();

if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

app.UseHttpsRedirection();
app.UseAuthorization();
app.MapControllers();
app.Run();
