using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace SmartRouteAPI_Service.Migrations
{
    /// <inheritdoc />
    public partial class AddStepCoordinates : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.AddColumn<string>(
                name: "EncodedPolyline",
                table: "TripSteps",
                type: "nvarchar(max)",
                nullable: true);

            migrationBuilder.AddColumn<double>(
                name: "EndLat",
                table: "TripSteps",
                type: "float",
                nullable: false,
                defaultValue: 0.0);

            migrationBuilder.AddColumn<double>(
                name: "EndLng",
                table: "TripSteps",
                type: "float",
                nullable: false,
                defaultValue: 0.0);

            migrationBuilder.AddColumn<double>(
                name: "StartLat",
                table: "TripSteps",
                type: "float",
                nullable: false,
                defaultValue: 0.0);

            migrationBuilder.AddColumn<double>(
                name: "StartLng",
                table: "TripSteps",
                type: "float",
                nullable: false,
                defaultValue: 0.0);
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropColumn(
                name: "EncodedPolyline",
                table: "TripSteps");

            migrationBuilder.DropColumn(
                name: "EndLat",
                table: "TripSteps");

            migrationBuilder.DropColumn(
                name: "EndLng",
                table: "TripSteps");

            migrationBuilder.DropColumn(
                name: "StartLat",
                table: "TripSteps");

            migrationBuilder.DropColumn(
                name: "StartLng",
                table: "TripSteps");
        }
    }
}
